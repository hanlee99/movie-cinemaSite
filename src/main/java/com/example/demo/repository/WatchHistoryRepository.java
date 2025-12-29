package com.example.demo.repository;

import com.example.demo.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId);

    List<WatchHistory> findByUserIdAndMovieId(Long userId, Long movieId);

    //특정 유저의 모든 관람기록 삭제 (회원 탈퇴용)
    @Modifying
    @Query("DELETE FROM WatchHistory wh WHERE wh.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}
