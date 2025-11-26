package com.example.demo.repository;

import com.example.demo.entity.CinemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CinemaRepository extends JpaRepository<CinemaEntity, Long> {
    @Query("SELECT DISTINCT c FROM CinemaEntity c " +
            "LEFT JOIN FETCH c.brandEntity " +
            "LEFT JOIN FETCH c.specialtyTheaterEntities")
    List<CinemaEntity> findAllWithBrand();
}
