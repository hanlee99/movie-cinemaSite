package com.example.demo.service;

import com.example.demo.dto.movie.DailyBoxOfficeResponse;
import com.example.demo.dto.movie.MovieResponseDto;
import com.example.demo.dto.movie.boxoffice.BoxOfficeItemDto;
import com.example.demo.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.example.demo.dto.movie.kmdb.KmdbMovieDto;
import com.example.demo.entity.MovieEntity;

import com.example.demo.external.adapter.KmdbAdapter;
import com.example.demo.external.adapter.KobisAdapter;
import com.example.demo.repository.MovieRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
    private final KmdbAdapter kmdbAdapter;
    private final KobisAdapter kobisAdapter;

    public List<MovieResponseDto> searchMovie(String title) {
        log.info("KMDB API 영화 검색 시작 - title: {}", title);
        try {
            List<KmdbMovieDto> kmdbMovies = kmdbAdapter.fetchMovies(title);
            log.info("KMDB API 영화 검색 완료 - {}건", kmdbMovies.size());

            return kmdbMovies.stream()
                    .map(dto -> {
                        MovieEntity temp = MovieEntity.from(dto);
                        return MovieResponseDto.from(temp);
                    })
                    .toList();
        } catch (Exception e) {
            log.error("KMDB API 영화 검색 실패 - title: {}", title, e);
            throw e;
        }
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

    @Cacheable(value = "dailyBoxOffice")
    public DailyBoxOfficeResultDto getDailyBoxOffice() {
        String yesterday = LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        log.info("KOBIS API 박스오피스 조회 - targetDt: {}", yesterday);

        try {
            DailyBoxOfficeResultDto result = kobisAdapter.getDailyBoxOffice(yesterday);
            log.info("KOBIS API 박스오피스 조회 완료 - {}건",
                    result.getDailyBoxOfficeList().size());
            return result;
        } catch (Exception e) {
            log.error("KOBIS API 박스오피스 조회 실패 - targetDt: {}", yesterday, e);
            throw e;
        }
    }

    public DailyBoxOfficeResponse getDailyBoxOfficeWithMovieInfo() {
        log.info("박스오피스 영화 정보 조회 시작");

        // 1. 박스오피스 정보 (캐시 or API)
        DailyBoxOfficeResultDto boxOffice = getDailyBoxOffice();

        // 2. 제목 리스트 추출
        List<String> titles = boxOffice.getDailyBoxOfficeList().stream()
                .map(BoxOfficeItemDto::getTitle)
                .toList();
        log.debug("박스오피스 제공 제목들: {}", titles);

        // 3. DB에서 영화 정보 조회
        List<MovieEntity> movies = movieRepository.findByTitleIn(titles);
        log.debug("DB 매칭된 영화 제목들: {}",
                movies.stream().map(MovieEntity::getTitle).toList());
        log.debug("DB 매칭 결과 - 박스오피스: {}건, DB 매칭: {}건", titles.size(), movies.size());

        // 4. Map으로 변환
        Map<String, MovieEntity> movieMap = movies.stream()
                .collect(Collectors.toMap(MovieEntity::getTitle, m -> m));

        // 5. 응답 생성
        DailyBoxOfficeResponse response = DailyBoxOfficeResponse.from(boxOffice, movieMap);

        log.info("박스오피스 영화 정보 조회 완료 - {}건", response.getMovies().size());

        return response;
    }
}

