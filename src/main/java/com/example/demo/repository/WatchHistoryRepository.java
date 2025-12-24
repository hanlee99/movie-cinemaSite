package com.example.demo.repository;

import com.example.demo.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId);

    List<WatchHistory> findByUserIdAndMovieId(Long userId, Long movieId);
}
