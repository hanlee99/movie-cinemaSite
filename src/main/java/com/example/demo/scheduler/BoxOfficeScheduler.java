package com.example.demo.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoxOfficeScheduler {
    @Scheduled(cron = "0 0 3 * * *")
    @CacheEvict(value = "dailyBoxOffice")
    public void evictDailyBoxOfficeCache() {
        log.info("박스오피스 캐시 삭제 (스케줄)");
    }
}
