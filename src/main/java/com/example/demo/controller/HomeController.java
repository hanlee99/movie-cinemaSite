package com.example.demo.controller;

import com.example.demo.dto.movie.DailyBoxOfficeResponse;
import com.example.demo.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.example.demo.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MovieService movieService;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("daily", movieService.getDailyBoxOfficeWithMovieInfo());
        model.addAttribute("nowPlaying", movieService.getNowPlaying(1, 20));
        model.addAttribute("upcoming", movieService.getUpcoming(1, 20));
        return "home";
    }

}
