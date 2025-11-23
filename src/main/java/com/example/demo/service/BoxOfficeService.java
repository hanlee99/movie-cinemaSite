package com.example.demo.service;

import com.example.demo.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.example.demo.external.adapter.KobisAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoxOfficeService {
    private final KobisAdapter kobisAdapter;

    /**
     * 일일 박스오피스 조회 (캐싱)
     */
    @Cacheable(value = "dailyBoxOffice", key = "'latest'")
    public DailyBoxOfficeResultDto getDailyBoxOffice() {
        String yesterday = LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        log.warn("🔥🔥🔥 캐시 미스! KOBIS API 호출! 🔥🔥🔥");
        log.info("KOBIS API 박스오피스 조회 - targetDt: {}", yesterday);

        try {
            DailyBoxOfficeResultDto result = kobisAdapter.getDailyBoxOffice(yesterday);
            log.info("KOBIS API 박스오피스 조회 완료 - {}건",
                    result.getDailyBoxOfficeList().size());
            return result;
        } catch (Exception e) {
            log.error("KOBIS API 박스오피스 조회 실패 - targetDt: {}", yesterday, e);
            throw e;
        }
    }
}
