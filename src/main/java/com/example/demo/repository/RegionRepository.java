package com.example.demo.repository;

import com.example.demo.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<RegionEntity, Long> {
    Optional<RegionEntity> findByRegionalLocalAndBasicLocal(String regionalLocal, String basicLocal);

}
