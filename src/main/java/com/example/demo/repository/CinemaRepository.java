package com.example.demo.repository;

import com.example.demo.entity.CinemaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CinemaRepository extends JpaRepository<CinemaEntity, Long> {
    @Query("SELECT DISTINCT c FROM CinemaEntity c " +
            "LEFT JOIN FETCH c.brandEntity " +
            "LEFT JOIN FETCH c.specialtyTheaterEntities")
    List<CinemaEntity> findAllWithBrand();

    @EntityGraph(attributePaths = {"brandEntity", "regionEntity", "specialtyTheaterEntities"})
    @Override
    List<CinemaEntity> findAll();

    @EntityGraph(attributePaths = {"brandEntity", "regionEntity", "specialtyTheaterEntities"})
    Optional<CinemaEntity> findById(Long id);

    @Query("SELECT c FROM CinemaEntity c " +
            "WHERE SQRT(POWER(c.xEpsg5174 - :x, 2) + POWER(c.yEpsg5174 - :y, 2)) <= :radius " +
            "ORDER BY SQRT(POWER(c.xEpsg5174 - :x, 2) + POWER(c.yEpsg5174 - :y, 2))")
    @EntityGraph(attributePaths = {"brandEntity", "regionEntity", "specialtyTheaterEntities"})
    List<CinemaEntity> findNearby(
            @Param("x") BigDecimal x,
            @Param("y") BigDecimal y,
            @Param("radius") BigDecimal radius
    );

    @Query("SELECT DISTINCT c FROM CinemaEntity c " +
            "JOIN c.specialtyTheaterEntities s " +
            "WHERE s.name IN ('아이맥스', '수퍼플렉스', 'DOLBY CINEMA')")
    @EntityGraph(attributePaths = {"brandEntity", "regionEntity", "specialtyTheaterEntities"})
    List<CinemaEntity> findSpecialTheaterCinemas();
}
