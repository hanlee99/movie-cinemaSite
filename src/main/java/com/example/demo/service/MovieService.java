package com.example.demo.service;

import com.example.demo.dto.movie.DailyBoxOfficeResponse;
import com.example.demo.dto.movie.MovieResponseDto;
import com.example.demo.dto.movie.boxoffice.BoxOfficeItemDto;
import com.example.demo.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.example.demo.entity.MovieEntity;

import com.example.demo.repository.MovieRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final BoxOfficeService boxOfficeService;

    public MovieEntity findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다: " + id));
    }

    public MovieEntity findByIdWithPeople(Long id) {
        return movieRepository.findByIdWithPeople(id)  // people 가져옴 (상세용)
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다: " + id));
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

        // 2. 제목 리스트 추출
        List<String> titles = boxOffice.getDailyBoxOfficeList().stream()
                .map(BoxOfficeItemDto::getTitle)
                .toList();
        log.debug("박스오피스 제공 제목들: {}", titles);

        // 3. 영화 정보 매칭
        Map<String, MovieEntity> movieMap = matchMovies(titles);

        // 4. 응답 생성
        DailyBoxOfficeResponse response = DailyBoxOfficeResponse.from(boxOffice, movieMap);
        log.info("박스오피스 영화 정보 조회 완료 - {}건", response.getMovies().size());

        return response;
    }

    private Map<String, MovieEntity> matchMovies(List<String> titles) {
        // 1차: 정확 매칭 (1번 쿼리)
        List<MovieEntity> exactMatches = movieRepository.findByTitleIn(titles);
        Map<String, MovieEntity> movieMap = exactMatches.stream()
                .collect(Collectors.toMap(MovieEntity::getTitle, m -> m));

        log.debug("정확 매칭: {}건 / {}건", movieMap.size(), titles.size());

        // 2차: 안 된 것만 LIKE 검색
        for (String title : titles) {
            if (movieMap.containsKey(title)) {
                continue;  // 이미 매칭됨
            }

            // 공백 제거하고 검색
            String normalized = title.replaceAll("\\s+", "");
            List<MovieEntity> found = movieRepository.findByTitleContains(normalized);

            if (!found.isEmpty()) {
                movieMap.put(title, found.get(0));
                log.debug("LIKE 매칭 성공: {}", title);
            } else {
                log.warn("매칭 실패: {}", title);
            }
        }

        log.debug("최종 매칭: {}건 / {}건", movieMap.size(), titles.size());
        return movieMap;
    }

}

