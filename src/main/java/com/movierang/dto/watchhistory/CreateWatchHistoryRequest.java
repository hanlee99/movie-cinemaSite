package com.movierang.dto.watchhistory;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class CreateWatchHistoryRequest {
    @NotNull(message = "영화 ID는 필수입니다")
    private Long movieId;

    @NotNull(message = "관람 날짜는 필수입니다")
    @PastOrPresent(message = "관람 날짜는 현재 또는 과거여야 합니다")
    private LocalDate watchedAt;

    // 극장 정보 (선택)
    private Long cinemaId;
    private String cinemaName;

    // 상영 시간 (선택)
    private LocalTime showTime;

    // 별점 (선택, 1-5)
    @Min(value = 1, message = "별점은 1 이상이어야 합니다")
    @Max(value = 5, message = "별점은 5 이하여야 합니다")
    private Integer rating;

    // 한줄평 (선택)
    @Size(max = 500, message = "한줄평은 500자 이하여야 합니다")
    private String comment;
}
