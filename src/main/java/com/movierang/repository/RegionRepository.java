package com.movierang.repository;

import com.movierang.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<RegionEntity, Long> {
    Optional<RegionEntity> findByRegionalLocalAndBasicLocal(String regionalLocal, String basicLocal);

}
