package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "watch_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private LocalDate watchedAt;

    // 극장 정보
    private Long cinemaId;

    @Column(length = 100)
    private String cinemaName;  // 비정규화 (극장명 저장)

    // 상영 시간
    private LocalTime showTime;

    // 별점 (1-5)
    @Column(columnDefinition = "INT CHECK (rating >= 1 AND rating <= 5)")
    private Integer rating;

    // 한줄평
    @Column(length = 500)
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void update(LocalDate watchedAt, Long cinemaId, String cinemaName,
                      LocalTime showTime, Integer rating, String comment) {
        if (watchedAt != null) this.watchedAt = watchedAt;
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.showTime = showTime;
        this.rating = rating;
        this.comment = comment;
    }
}
