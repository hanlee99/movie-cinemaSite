package com.example.demo.repository;

import com.example.demo.entity.MovieStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieStatsRepository extends JpaRepository<MovieStats, Long> {
}
