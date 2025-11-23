package com.example.demo.controller;

import com.example.demo.dto.movie.DailyBoxOfficeResponse;
import com.example.demo.dto.movie.MovieResponseDto;
import com.example.demo.entity.MovieEntity;
import com.example.demo.service.MovieService;
import com.example.demo.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;
    private final MovieSyncService movieSyncService;

    @GetMapping("/now-playing")
    public ResponseEntity<Page<MovieResponseDto>> getNowPlaying(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(movieService.getNowPlaying(page, size));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<MovieResponseDto>> getUpcoming(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(movieService.getUpcoming(page, size));
    }

    @GetMapping("/boxoffice/daily")
    public DailyBoxOfficeResponse getDailyBoxOffice() {
        log.info("GET /api/movies/box-office/daily 요청");
        return movieService.getDailyBoxOfficeWithMovieInfo();
    }

    @GetMapping("/search")
    public List<MovieResponseDto> searchMovies(@RequestParam String title) {
        return movieService.findMoviesByTitle(title);
    }
}
