package com.example.demo.controller.admin;

import com.example.demo.repository.MovieRepository;
import com.example.demo.service.MovieSyncService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminSyncController {
    private final MovieSyncService movieSyncService;
    private final CacheManager cacheManager;
    private final MovieRepository movieRepository;

     // KOBIS 박스오피스 데이터 동기화
    @DeleteMapping("/cache/boxoffice")
    public ResponseEntity<?> deleteBoxOfficeCache() {
        try {
            cacheManager.getCache("dailyBoxOffice").clear();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "박스오피스 캐시 삭제 완료. 다음 요청 시 최신 데이터 자동 갱신"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

     // KMDB 영화 상세정보 동기화

    @PostMapping("/sync/year")
    public ResponseEntity<?> syncKmdb(@RequestParam int year) {
        try {
            long startTime = System.currentTimeMillis();
            long beforeCount = movieRepository.count();

            movieSyncService.syncMoviesByYear(year);

            long afterCount = movieRepository.count();
            long duration = (System.currentTimeMillis() - startTime) / 1000;

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "KMDB 영화 정보 동기화 완료",
                    "addedCount", afterCount - beforeCount,
                    "totalCount", afterCount,
                    "durationSeconds", duration
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/sync-kmdb/range")
    public ResponseEntity<?> syncKmdb(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            // MovieSyncService의 KMDB 동기화 메서드 호출
            movieSyncService.syncMoviesByDay(startDate, endDate);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "KMDB 영화 정보 동기화 완료"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }

     //테스트용 엔드포인드
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "관리자 인증 성공!"
        ));
    }
}
