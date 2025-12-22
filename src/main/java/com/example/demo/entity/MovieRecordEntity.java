package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie_record", indexes = {
        @Index(name = "idx_user_watched_date", columnList = "user_id, watched_date"),
        @Index(name = "idx_user_movie", columnList = "user_id, movie_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MovieRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @Column(nullable = false)
    private LocalDate watchedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @Column(length = 100)
    private String cinemaName;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private WatchStatus status = WatchStatus.WATCHED;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateRecord(LocalDate watchedDate, BrandEntity brand,
                             String cinemaName, Integer rating, String review) {
        this.watchedDate = watchedDate;
        this.brand = brand;
        this.cinemaName = cinemaName;
        this.rating = rating;
        this.review = review;
    }

    public void updateRating(Integer rating) {
        this.rating = rating;
        validateRating(rating);
        this.rating = rating;
    }
    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1~5 사이여야 합니다. 입력값: " + rating);
        }
    }
    public void updateReview(String review) {
        this.review = review;
    }

    public void updateStatus(WatchStatus status) {
        this.status = status;
    }
}
