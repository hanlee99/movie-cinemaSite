package com.example.demo.dto.watchhistory;

import com.example.demo.entity.WatchHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WatchHistoryResponse {
    private Long id;
    private Long movieId;
    private LocalDate watchedAt;
    private String comment;
    private LocalDateTime createdAt;

    public static WatchHistoryResponse from(WatchHistory entity) {
        return new WatchHistoryResponse(
                entity.getId(),
                entity.getMovieId(),
                entity.getWatchedAt(),
                entity.getComment(),
                entity.getCreatedAt()
        );
    }
}
