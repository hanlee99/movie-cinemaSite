package com.example.demo.controller;

import com.example.demo.dto.cinema.CinemaResponseDto;
import com.example.demo.dto.movie.MovieResponseDto;
import com.example.demo.service.CinemaService;
import com.example.demo.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
    private final CinemaService cinemaService;
    @Value("${api.kakao.key}")
    private String kakaoApiKey;

    @GetMapping("/")
    public String home(Model model) {


        model.addAttribute("daily", movieService.getDailyBoxOfficeWithMovieInfo());
        model.addAttribute("nowPlaying", movieService.getNowPlaying(1, 20));
        model.addAttribute("upcoming", movieService.getUpcoming(1, 20));

        // 특별관 극장 30개
        List<CinemaResponseDto> specialCinemas = cinemaService.getSpecialTheaterCinemas();
        log.info("특별관 극장 개수: {}", specialCinemas.size());

        model.addAttribute("cinemas", specialCinemas);
        model.addAttribute("KAKAO_API_KEY", kakaoApiKey);


        return "home";
    }

    @GetMapping("/cinema/list")
    public String cinemaList() {
        return "cinema-list";  // cinema-list.html 반환
    }

    @GetMapping("/cinema/{id}")
    public String cinemaDetail(@PathVariable Long id, Model model) {
        model.addAttribute("cinema", cinemaService.getCinemaById(id));
        return "cinema/detail";
    }

    @GetMapping("/movies/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        log.info("영화 상세 페이지 요청 - id: {}", id);

        MovieResponseDto movie = movieService.getMovieDetail(id);
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
