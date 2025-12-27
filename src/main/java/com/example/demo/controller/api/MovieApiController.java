package com.example.demo.controller.api;

import com.example.demo.dto.movie.DailyBoxOfficeResponse;
import com.example.demo.dto.movie.MovieResponseDto;
import com.example.demo.service.MovieService;
import com.example.demo.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieApiController {
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
    public ResponseEntity<DailyBoxOfficeResponse> getDailyBoxOffice() {
        log.info("GET /api/movie/boxoffice/daily 요청");
        return ResponseEntity.ok(movieService.getDailyBoxOfficeWithMovieInfo());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponseDto>> searchMovies(@RequestParam String title) {
        return ResponseEntity.ok(movieService.findMoviesByTitle(title));
    }
}
