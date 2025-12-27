package com.example.demo.dto.watchhistory;

import com.example.demo.entity.MovieEntity;
import com.example.demo.entity.WatchHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class WatchHistoryResponse {
    private Long id;

    // 영화 정보
    private Long movieId;
    private String movieTitle;
    private String posterUrl;

    // 관람 정보
    private LocalDate watchedAt;

    // 극장 정보
    private Long cinemaId;
    private String cinemaName;

    // 상영 시간
    private LocalTime showTime;

    // 별점
    private Integer rating;

    // 한줄평
    private String comment;

    // 생성/수정 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 변환 (영화 정보 없음)
    public static WatchHistoryResponse from(WatchHistory entity) {
        return WatchHistoryResponse.builder()
                .id(entity.getId())
                .movieId(entity.getMovieId())
                .movieTitle(null)  // 영화 정보 없음
                .posterUrl(null)
                .watchedAt(entity.getWatchedAt())
                .cinemaId(entity.getCinemaId())
                .cinemaName(entity.getCinemaName())
                .showTime(entity.getShowTime())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // 영화 정보 포함 변환
    public static WatchHistoryResponse from(WatchHistory entity, MovieEntity movie) {
        return WatchHistoryResponse.builder()
                .id(entity.getId())
                .movieId(entity.getMovieId())
                .movieTitle(movie != null ? movie.getTitle() : "정보 없음")
                .posterUrl(movie != null ? movie.getPoster() : null)
                .watchedAt(entity.getWatchedAt())
                .cinemaId(entity.getCinemaId())
                .cinemaName(entity.getCinemaName())
                .showTime(entity.getShowTime())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
