package com.movierang.integration.service;

import com.movierang.dto.movie.MovieResponseDto;
import com.movierang.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * MovieService 통합 테스트
 * - 실제 DB와 외부 API를 사용하는 테스트
 * - 환경에 따라 실패할 수 있음 (데이터 의존)
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("MovieService 통합 테스트")
class MovieServiceIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Test
    @DisplayName("일간 박스오피스 조회 성공")
    void getDailyBoxOffice_Success() {
        // when
        var result = movieService.getDailyBoxOfficeWithMovieInfo();

        // then
        assertNotNull(result);
        assertNotNull(result.getMovies());
    }

    @Test
    @DisplayName("영화 상세 조회 성공")
    void getMovieDetail_Success() {
        // given
        Long movieId = 1L;

        // when
        MovieResponseDto result = movieService.getMovieDetail(movieId);

        // then
        assertNotNull(result);
        assertNotNull(result.getTitle());
    }

    @Test
    @DisplayName("영화 제목 검색 성공")
    void findMoviesByTitle_Success() {
        // given
        String keyword = "범죄";

        // when
        List<MovieResponseDto> results = movieService.findMoviesByTitle(keyword);

        // then
        assertNotNull(results);
    }

    @Test
    @DisplayName("존재하지 않는 영화 조회 시 예외 발생")
    void getMovieDetail_NotFound() {
        // given
        Long invalidId = 999999L;

        // when & then
        assertThrows(RuntimeException.class, () -> {
            movieService.getMovieDetail(invalidId);
        });
    }
}
