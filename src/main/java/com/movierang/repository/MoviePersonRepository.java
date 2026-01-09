package com.movierang.repository;

import com.movierang.entity.MovieEntity;
import com.movierang.entity.MoviePersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoviePersonRepository extends JpaRepository<MoviePersonEntity, Long> {
    List<MoviePersonEntity> findAllByMovie(MovieEntity movie);

}
