package com.example.demo.service;

import com.example.demo.dto.movie.DailyBoxOfficeResponse;
import com.example.demo.dto.movie.MovieResponseDto;
import com.example.demo.dto.movie.boxoffice.BoxOfficeItemDto;
import com.example.demo.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.example.demo.entity.MovieEntity;
import com.example.demo.entity.MovieStats;

import com.example.demo.exception.MovieNotFoundException;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.MovieStatsRepository;

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

    /**
     * 영화 상세 조회 (조회수 증가 포함)
     *
     * @param id 영화 ID
     * @return 영화 상세 정보
     */
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

    /**
     * 조회수 증가 (내부 데이터 수집용)
     *
     * 동시성 제어:
     * - @Version으로 낙관적 락 적용
     * - 여러 사용자가 동시에 조회해도 Lost Update 방지
     * - OptimisticLockingFailureException 발생 시 @Retryable이 자동 재시도
     */
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
        int year1 = Integer.parseInt(repRlsDate.substring(0, 4));
        int year2 = kobisOpenDt.getYear();
        return Math.abs(year1 - year2);
    }

}

