package com.movierang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MovieStats {

    @Id
    @Column(name = "movie_id")
    private Long movieId;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long wishlistCount;

    @Version
    private Long version;

    public void incrementView() {
        this.viewCount++;
    }

    public void incrementWishlist() {
        this.wishlistCount++;
    }

    public void decrementWishlist() {
        if (this.wishlistCount > 0) {
            this.wishlistCount--;
        }
    }

    public static MovieStats createDefault(Long movieId) {
        return MovieStats.builder()
                .movieId(movieId)
                .viewCount(0L)
                .wishlistCount(0L)
                .build();
    }
}
