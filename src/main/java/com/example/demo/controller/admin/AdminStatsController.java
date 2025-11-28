package com.example.demo.controller.admin;

import com.example.demo.entity.MovieEntity;
import com.example.demo.repository.CinemaRepository;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/stats")
@RequiredArgsConstructor
public class AdminStatsController {
    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;
    private final CinemaRepository cinemaRepository;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        return ResponseEntity.ok(Map.of(
                "totalMovies", movieRepository.count(),
                "totalPersons", personRepository.count(),
                "totalCinemas", cinemaRepository.count()
        ));
    }


}
