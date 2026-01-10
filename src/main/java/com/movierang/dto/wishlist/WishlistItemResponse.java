package com.movierang.dto.wishlist;

import com.movierang.entity.MovieEntity;
import com.movierang.entity.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class WishlistItemResponse {
    private Long wishlistId;
    private Long movieId;
    private String title;
    private String poster;
    private String genre;
    private String repRlsDate;
    private LocalDateTime createdAt;

    public static WishlistItemResponse from(Wishlist wishlist, MovieEntity movie) {
        return WishlistItemResponse.builder()
                .wishlistId(wishlist.getId())
                .movieId(movie.getId())
                .title(movie.getTitle())
                .poster(movie.getPoster() != null ? movie.getPoster() : "")
                .genre(movie.getGenre() != null ? movie.getGenre() : "")
                .repRlsDate(movie.getRepRlsDate() != null ? movie.getRepRlsDate() : "")
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}
