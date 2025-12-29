package com.example.demo.dto.watchhistory;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class UpdateWatchHistoryRequest {
    @NotNull(message = "관람 날짜는 필수입니다")
    @PastOrPresent(message = "관람 날짜는 현재 또는 과거여야 합니다")
    private LocalDate watchedAt;

    private Long cinemaId;
    private String cinemaName;

    private LocalTime showTime;

    @Min(value = 1, message = "별점은 1 이상이어야 합니다")
    @Max(value = 5, message = "별점은 5 이하여야 합니다")
    private Integer rating;

    @Size(max = 500, message = "한줄평은 500자 이하여야 합니다")
    private String comment;
}
