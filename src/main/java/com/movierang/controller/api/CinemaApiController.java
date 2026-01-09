package com.movierang.controller.api;

import com.movierang.dto.cinema.CinemaResponseDto;
import com.movierang.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cinema")
@RequiredArgsConstructor
public class CinemaApiController {
    private final CinemaService cinemaService;

    @GetMapping("/all")
    public ResponseEntity<List<CinemaResponseDto>> getAllCinemas() {
        return ResponseEntity.ok(cinemaService.getAllCinemas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponseDto> getCinemaById(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<CinemaResponseDto>> getNearby(
            @RequestParam BigDecimal x,
            @RequestParam BigDecimal y,
            @RequestParam(defaultValue = "5000") BigDecimal radius
    ) {
        return ResponseEntity.ok(cinemaService.getNearby(x, y, radius));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CinemaResponseDto>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(cinemaService.searchByKeyword(keyword));
    }
}
