package com.example.demo.runner;

import com.example.demo.external.adapter.KmdbAdapter;
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
    private final KmdbAdapter kmdbAdapter;

    @Override
    public void run(String... args) throws Exception{
        System.out.println("🎬 KMDB 영화 동기화 시작...");
        movieSyncService.syncMoviesByYear(2025);
        System.out.println("✅ 동기화 완료!");
        movieSyncService.syncMovieByTitle("코렐라인");


    }
}
