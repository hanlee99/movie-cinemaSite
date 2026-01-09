package com.movierang.repository;

import com.movierang.entity.MovieStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieStatsRepository extends JpaRepository<MovieStats, Long> {
}
