package com.example.demo.repository;

import com.example.demo.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {


    //특정 유저의 찜 목록 조회 (최신순)
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    //특정 유저의 모든 찜 목록 조회 (회원 탈퇴용)
    List<Wishlist> findByUserId(Long userId);

    //특정 유저가 특정 영화를 찜했는지 확인
    Optional<Wishlist> findByUserIdAndMovieId(Long userId, Long movieId);

     //특정 유저가 특정 영화를 찜했는지 여부
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    //특정 유저의 특정 영화 찜 삭제
    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    //특정 유저의 모든 찜 삭제 (회원 탈퇴용)
    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}
