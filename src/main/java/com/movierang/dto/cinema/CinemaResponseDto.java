package com.movierang.dto.cinema;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CinemaResponseDto {
    private Long cinemaId;
    private String cinemaName;
    private String brand;
    private String region;
    private String classificationRegion;
    private String streetAddress;
    private String loadAddress;
    private List<String> specialtyTheaters;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
