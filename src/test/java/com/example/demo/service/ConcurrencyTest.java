package com.example.demo.service;

import com.example.demo.entity.MovieEntity;
import com.example.demo.entity.MovieStats;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.MovieStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Disabled("H2 데이터베이스는 실제 동시성 환경에서 Optimistic Locking을 완벽히 지원하지 않음. 실제 DB 또는 수동 테스트 필요.")
class MovieConcurrencyTest {

    @Autowired
    private MovieService movieService;

    @Autowired MovieRepository movieRepository;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private MovieStatsRepository movieStatsRepository;

    private Long testMovieId;

    @BeforeEach
    void setUp() {
        MovieEntity movie = MovieEntity.builder()
                .title("테스트 영화")
                .repRlsDate("20240101")
                .build();
        testMovieId = movieRepository.save(movie).getId();
        movieStatsRepository.deleteAll();
    }

    @Test
    @DisplayName("100명이 동시에 조회하면 조회수가 정확히 100 증가한다")
    void 조회수_동시성_테스트() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    movieService.getMovieDetail(testMovieId);
                } catch (Exception e) {
                    // ignore
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        // then
        MovieStats stats = movieStatsRepository.findById(testMovieId).orElseThrow();
        assertThat(stats.getViewCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("100명이 동시에 찜하면 정확히 100개 생성된다")
    void 찜_동시성_테스트() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    wishlistService.toggleWishlist(userId, testMovieId);
                } catch (Exception e) {
                    // ignore
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        // then
        MovieStats stats = movieStatsRepository.findById(testMovieId).orElseThrow();
        assertThat(stats.getWishlistCount()).isEqualTo(100);
    }
}
