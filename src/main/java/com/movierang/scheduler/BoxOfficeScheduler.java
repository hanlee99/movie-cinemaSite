package com.movierang.scheduler;

import com.movierang.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoxOfficeScheduler {

    private final MovieSyncService movieSyncService;

    @Scheduled(cron = "0 0 3 * * *")
    @CacheEvict(value = "dailyBoxOffice", allEntries = true)
    public void dailyUpdate() {
        int year = LocalDate.now().getYear();
        log.info("일일 업데이트 시작 - 박스오피스 캐시 삭제, KMDB {}년 sync", year);

        movieSyncService.syncMoviesByYear(year);

        log.info("일일 업데이트 완료");
    }
}
