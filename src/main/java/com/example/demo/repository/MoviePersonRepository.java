package com.example.demo.repository;

import com.example.demo.entity.MovieEntity;
import com.example.demo.entity.MoviePersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoviePersonRepository extends JpaRepository<MoviePersonEntity, Long> {
    List<MoviePersonEntity> findAllByMovie(MovieEntity movie);

}
