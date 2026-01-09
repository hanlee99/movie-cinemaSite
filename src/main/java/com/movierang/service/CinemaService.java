package com.movierang.service;

import com.movierang.dto.cinema.CinemaResponseDto;
import com.movierang.entity.CinemaEntity;
import com.movierang.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<CinemaResponseDto> searchByKeyword(String keyword) {
        return cinemaRepository.searchByName(keyword)
                .stream()
                .map(CinemaEntity::toDto)
                .limit(10)  // 최대 10개까지만 반환
                .collect(Collectors.toList());
    }
}
