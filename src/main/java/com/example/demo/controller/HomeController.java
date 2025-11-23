package com.example.demo.controller;

import com.example.demo.dto.movie.MovieResponseDto;
import com.example.demo.entity.MovieEntity;
import com.example.demo.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final MovieService movieService;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("daily", movieService.getDailyBoxOfficeWithMovieInfo());
        model.addAttribute("nowPlaying", movieService.getNowPlaying(1, 20));
        model.addAttribute("upcoming", movieService.getUpcoming(1, 20));
        return "home";
    }

    @GetMapping("/movies/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        log.info("영화 상세 페이지 요청 - id: {}", id);

        MovieEntity movie = movieService.findByIdWithPeople(id);
        model.addAttribute("movie", movie);

        return "detail";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.isBlank()) {
            log.info("영화 검색 요청 - keyword: {}", keyword);
            List<MovieResponseDto> results = movieService.findMoviesByTitle(keyword);
            model.addAttribute("results", results);
            model.addAttribute("keyword", keyword);
        }

        return "search";
    }
}
