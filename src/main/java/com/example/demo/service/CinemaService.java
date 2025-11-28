package com.example.demo.service;

import com.example.demo.dto.cinema.CinemaResponseDto;
import com.example.demo.entity.CinemaEntity;
import com.example.demo.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CinemaService {
    private final CinemaRepository cinemaRepository;

    public List<CinemaResponseDto> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(CinemaEntity::toDto)
                .collect(Collectors.toList());
    }

    public CinemaResponseDto getCinemaById(Long id) {
        return cinemaRepository.findById(id)
                .map(CinemaEntity::toDto)
                .orElseThrow(() -> new RuntimeException("Cinema not found"));
    }

    public List<CinemaResponseDto> getNearby(BigDecimal x, BigDecimal y, BigDecimal radius) {
        return cinemaRepository.findNearby(x, y, radius)
                .stream()
                .map(CinemaEntity::toDto)
                .collect(Collectors.toList());
    }


    public List<CinemaResponseDto> getSpecialTheaterCinemas() {
        return cinemaRepository.findSpecialTheaterCinemas()
                .stream()
                .map(CinemaEntity::toDto)
                //.limit(30)
                .collect(Collectors.toList());
    }
}
