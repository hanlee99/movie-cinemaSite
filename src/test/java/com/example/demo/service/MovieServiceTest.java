package com.example.demo.service;

import com.example.demo.dto.movie.MovieResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MovieServiceTest {
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
