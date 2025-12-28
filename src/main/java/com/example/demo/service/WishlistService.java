package com.example.demo.service;

import com.example.demo.entity.MovieStats;
import com.example.demo.entity.Wishlist;
import com.example.demo.exception.MovieNotFoundException;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.MovieStatsRepository;
import com.example.demo.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final MovieRepository movieRepository;
    private final MovieStatsRepository movieStatsRepository;

    @Transactional
    @Retryable(
        value = OptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 50)
    )
    public boolean toggleWishlist(Long userId, Long movieId) {
        // 1. 영화 존재 확인
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(movieId);
        }

        // 2. 찜 여부 확인
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndMovieId(userId, movieId);

        boolean isWishlisted;

        if (existing.isPresent()) {
            // 찜 취소
            wishlistRepository.delete(existing.get());
            decrementWishlistCount(movieId);
            isWishlisted = false;
            log.info("찜 취소 - userId: {}, movieId: {}", userId, movieId);
        } else {
            // 찜 추가
            try {
                Wishlist wishlist = Wishlist.builder()
                        .userId(userId)
                        .movieId(movieId)
                        .build();
                wishlistRepository.save(wishlist);
                incrementWishlistCount(movieId);
                isWishlisted = true;
                log.info("찜 추가 - userId: {}, movieId: {}", userId, movieId);
            } catch (DataIntegrityViolationException e) {
                // 유니크 제약 위반 (동시에 두 번 클릭한 경우)
                // 이미 찜했으므로 취소로 처리
                log.warn("중복 찜 시도 감지 - userId: {}, movieId: {}", userId, movieId);
                return toggleWishlist(userId, movieId);  // 재귀 호출로 취소 처리
            }
        }

        return isWishlisted;
    }

    private void incrementWishlistCount(Long movieId) {
        MovieStats stats = movieStatsRepository.findById(movieId)
                .orElseGet(() -> MovieStats.createDefault(movieId));

        stats.incrementWishlist();
        movieStatsRepository.save(stats);

        log.debug("찜 개수 증가 - movieId: {}, wishlistCount: {}", movieId, stats.getWishlistCount());
    }

    private void decrementWishlistCount(Long movieId) {
        MovieStats stats = movieStatsRepository.findById(movieId)
                .orElseGet(() -> MovieStats.createDefault(movieId));

        stats.decrementWishlist();
        movieStatsRepository.save(stats);

        log.debug("찜 개수 감소 - movieId: {}, wishlistCount: {}", movieId, stats.getWishlistCount());
    }

    public boolean isWishlisted(Long userId, Long movieId) {
        return wishlistRepository.existsByUserIdAndMovieId(userId, movieId);
    }
}
