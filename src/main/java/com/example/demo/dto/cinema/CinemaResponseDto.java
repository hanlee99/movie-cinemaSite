package com.example.demo.dto.cinema;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaResponseDto {
    private Long cinemaId;             // 영화관 PK
    private String name;               // 영화관 이름
    private String brand;              // 브랜드 이름
    private String region;             // 지역 (서울, 경기 등)
    private String address;            // 전체 주소
    private List<String> specialtyTheaters;  // IMAX, 4DX 등
    //private Double latitude;           // 위도
    //private Double longitude;          // 경도
}
