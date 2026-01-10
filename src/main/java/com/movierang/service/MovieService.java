package com.movierang.service;

import com.movierang.dto.movie.DailyBoxOfficeResponse;
import com.movierang.dto.movie.MovieResponseDto;
import com.movierang.dto.movie.boxoffice.BoxOfficeItemDto;
import com.movierang.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.movierang.entity.MovieEntity;
import com.movierang.entity.MovieStats;

import com.movierang.exception.MovieNotFoundException;
import com.movierang.repository.MovieRepository;
import com.movierang.repository.MovieStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieStatsRepository movieStatsRepository;
    private final BoxOfficeService boxOfficeService;

    public MovieEntity findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public MovieEntity findByIdWithPeople(Long id) {
        return movieRepository.findByIdWithPeople(id)  // people 가져옴 (상세용)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Transactional
    @Retryable(
        value = OptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 50)
    )
    public MovieResponseDto getMovieDetail(Long id) {
        // 1. 영화 정보 조회
        MovieEntity entity = findByIdWithPeople(id);

        // 2. 조회수 증가 (동시성 제어)
        incrementViewCount(id);

        return MovieResponseDto.from(entity);
    }

    private void incrementViewCount(Long movieId) {
        MovieStats stats = movieStatsRepository.findById(movieId)
                .orElseGet(() -> MovieStats.createDefault(movieId));

        stats.incrementView();
        movieStatsRepository.save(stats);

        log.debug("영화 조회수 증가 - movieId: {}, viewCount: {}", movieId, stats.getViewCount());
    }

    public List<MovieResponseDto> findMoviesByTitle(String title) {
        log.debug("DB 영화 검색 - title: {}", title);
        List<MovieEntity> movies = movieRepository.findAllByTitleContainingIgnoreCase(title);
        log.debug("DB 영화 검색 결과 - {}건", movies.size());

        return movies.stream()
                .map(MovieResponseDto::from)
                .toList();
    }

    // DB 영화 검색 (페이징)
    public Page<MovieResponseDto> searchMovies(String keyword, int page, int size) {
        log.debug("영화 검색 - keyword: {}, page: {}, size: {}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        return movieRepository.searchByKeyword(keyword, pageable)
                .map(MovieResponseDto::summary);
    }

    public Page<MovieResponseDto> getNowPlaying(int page, int size) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        log.debug("현재 상영작 조회 - page: {}, size: {}, date: {}", page, size, today);

        Pageable pageable = PageRequest.of(page, size);

        return movieRepository.findNowPlayingMovies(today, pageable)
                .map(MovieResponseDto::summary);
    }

    public Page<MovieResponseDto> getUpcoming(int page, int size) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        log.debug("개봉 예정작 조회 - page: {}, size: {}, date: {}", page, size, today);

        Pageable pageable = PageRequest.of(page, size);

        return movieRepository.findUpcomingMovies(today, pageable)
                .map(MovieResponseDto::summary);
    }

    public DailyBoxOfficeResponse getDailyBoxOfficeWithMovieInfo() {
        log.info("박스오피스 영화 정보 조회 시작");

        // 1. 박스오피스 정보 (캐시)
        DailyBoxOfficeResultDto boxOffice = boxOfficeService.getDailyBoxOffice();

        // 2. 영화 정보 매칭 (제목 리스트 추출 부분 삭제)
        Map<String, MovieEntity> movieMap = matchMovies(boxOffice.getDailyBoxOfficeList());

        // 3. 응답 생성
        DailyBoxOfficeResponse response = DailyBoxOfficeResponse.from(boxOffice, movieMap);
        log.info("박스오피스 영화 정보 조회 완료 - {}건", response.getMovies().size());

        return response;
    }

    private Map<String, MovieEntity> matchMovies(List<BoxOfficeItemDto> boxOfficeItems) {
        List<String> titles = boxOfficeItems.stream()
                .map(BoxOfficeItemDto::getTitle)
                .toList();

        Map<String, LocalDate> titleToOpenDt = boxOfficeItems.stream()  // String -> LocalDate
                .collect(Collectors.toMap(
                        BoxOfficeItemDto::getTitle,
                        BoxOfficeItemDto::getOpenDt,
                        (v1, v2) -> v1
                ));

        log.debug("박스오피스 제공 제목들: {}", titles);

        // 1차: 정확 매칭
        List<MovieEntity> exactMatches = movieRepository.findByTitleIn(titles);

        Map<String, MovieEntity> movieMap = exactMatches.stream()
                .collect(Collectors.toMap(
                        MovieEntity::getTitle,
                        m -> m,
                        (m1, m2) -> selectClosestByYear(m1, m2, titleToOpenDt.get(m1.getTitle()))
                ));

        log.debug("정확 매칭: {}건 / {}건", movieMap.size(), titles.size());

        // 2차: LIKE 검색
        for (BoxOfficeItemDto item : boxOfficeItems) {
            if (movieMap.containsKey(item.getTitle())) continue;

            //코비스데이터와 kmdb 매칭 주의
            String normalized = item.getTitle().replaceAll("\\s+", "");
            //List<MovieEntity> found = movieRepository.findByTitleContains(normalized);
            //movieEtc에서 포함
            List<MovieEntity> found = movieRepository.findByTitleEtcContaining(normalized);

            if (!found.isEmpty()) {
                MovieEntity best = found.stream()
                        .min(Comparator.comparingInt(m ->
                                yearDiff(m.getRepRlsDate(), item.getOpenDt())))
                        .orElseThrow(() -> new MovieNotFoundException(item.getTitle()));
                movieMap.put(item.getTitle(), best);
                log.debug("LIKE 매칭: {}", item.getTitle());
            } else {
                log.warn("매칭 실패: {}", item.getTitle());
            }
        }

        log.debug("최종 매칭: {}건 / {}건", movieMap.size(), titles.size());
        return movieMap;
    }

    private MovieEntity selectClosestByYear(MovieEntity m1, MovieEntity m2, LocalDate openDt) {
        int diff1 = yearDiff(m1.getRepRlsDate(), openDt);
        int diff2 = yearDiff(m2.getRepRlsDate(), openDt);
        log.debug("중복: {} | KOBIS: {} | DB1: {} | DB2: {}",
                m1.getTitle(), openDt, m1.getRepRlsDate(), m2.getRepRlsDate());
        return diff1 <= diff2 ? m1 : m2;
    }

    private int yearDiff(String repRlsDate, LocalDate kobisOpenDt) {
        if (repRlsDate == null || repRlsDate.length() < 4) {
            return Integer.MAX_VALUE;
        }
        try {
            int year1 = Integer.parseInt(repRlsDate.substring(0, 4));
            int year2 = kobisOpenDt.getYear();
            return Math.abs(year1 - year2);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

}