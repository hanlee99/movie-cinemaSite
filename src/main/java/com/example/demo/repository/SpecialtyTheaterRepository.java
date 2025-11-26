package com.example.demo.repository;

import com.example.demo.entity.SpecialtyTheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialtyTheaterRepository extends JpaRepository<SpecialtyTheaterEntity, Long> {
    Optional<SpecialtyTheaterEntity> findByName(String name);
}
