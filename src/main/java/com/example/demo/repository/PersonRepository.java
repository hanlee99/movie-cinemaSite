package com.example.demo.repository;

import com.example.demo.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    Optional<PersonEntity> findByKmdbPersonId(String kmdbPersonId);

}
