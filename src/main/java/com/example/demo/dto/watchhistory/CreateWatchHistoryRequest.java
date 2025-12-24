package com.example.demo.dto.watchhistory;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateWatchHistoryRequest {
    private Long movieId;
    private LocalDate watchedAt;
    private String comment;
}
