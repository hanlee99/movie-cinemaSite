package com.example.demo.repository;

import com.example.demo.entity.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    boolean existsByDocId(String docId);

    Optional<MovieEntity> findByMovieId(String movieId);

    /*@Query("SELECT m FROM MovieEntity m LEFT JOIN FETCH m.people WHERE m.id = :id")*/

    @Query("SELECT DISTINCT m FROM MovieEntity m " +
            "LEFT JOIN FETCH m.people mp " +
            "LEFT JOIN FETCH mp.person " +
            "WHERE m.id = :id")
    Optional<MovieEntity> findByIdWithPeople(@Param("id") Long id);

    // 제목 리스트로 조회
    List<MovieEntity> findByTitleIn(List<String> titles);

    @Query("SELECT m FROM MovieEntity m WHERE m.title LIKE %:title%")
    List<MovieEntity> findByTitleContains(@Param("title") String title);

    // 또는 제목 + 개봉일
    Optional<MovieEntity> findByTitleAndRepRlsDate(String title, String repRlsDate);

    List<MovieEntity> findAllByTitleContainingIgnoreCase(String title);

    // 현재 상영작 조회 (00 처리)
    @Query(value = """
    SELECT * FROM movie
    WHERE reprlsdate <= :today
    ORDER BY
        CASE
            WHEN SUBSTRING(reprlsdate, 7, 2) = '00'
            THEN SUBSTRING(reprlsdate, 1, 6) || '99'
            ELSE reprlsdate
        END DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM movie
    WHERE reprlsdate <= :today
    """,
            nativeQuery = true)
    Page<MovieEntity> findNowPlayingMovies(@Param("today") String today, Pageable pageable);

    // 개봉 예정작 조회 (00 처리)
    @Query(value = """
    SELECT * FROM movie
    WHERE reprlsdate > :today
    ORDER BY
        CASE
            WHEN SUBSTRING(reprlsdate, 7, 2) = '00'
            THEN SUBSTRING(reprlsdate, 1, 6) || '99'
            ELSE reprlsdate
        END ASC
    """,
            countQuery = """
    SELECT COUNT(*) FROM movie
    WHERE reprlsdate > :today
    """,
            nativeQuery = true)
    Page<MovieEntity> findUpcomingMovies(@Param("today") String today, Pageable pageable);

    // 영화 검색 (제목, 영어제목 포함)
    @Query(value = """
    SELECT * FROM movie
    WHERE title ILIKE %:keyword%
       OR title_eng ILIKE %:keyword%
    ORDER BY
        CASE
            WHEN SUBSTRING(reprlsdate, 7, 2) = '00'
            THEN SUBSTRING(reprlsdate, 1, 6) || '99'
            ELSE reprlsdate
        END DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM movie
    WHERE title ILIKE %:keyword%
       OR title_eng ILIKE %:keyword%
    """,
            nativeQuery = true)
    Page<MovieEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
