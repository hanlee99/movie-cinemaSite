package com.example.demo.runner;

import com.example.demo.external.adapter.KmdbAdapter;
import com.example.demo.repository.MovieRepository;
import com.example.demo.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")  // local 프로파일에서만 실행
public class MovieSyncRunner implements CommandLineRunner {
    private final MovieSyncService movieSyncService;
    private final MovieRepository movieRepository;
    @Override
    public void run(String... args) throws Exception{
        if (movieRepository.count() > 0) {
            log.info("⏭ 기존 영화 데이터가 존재하여 초기 동기화를 건너뜁니다.");
            return;
        }

        log.info("🎬 KMDB 영화 동기화 시작...");
        movieSyncService.syncMoviesByYear(2025);
        log.info("✅ 동기화 완료!");
        //movieSyncService.syncMovieByTitle("가타카");


    }
}
