package com.example.demo.controller;

import com.example.demo.dto.watchhistory.CreateWatchHistoryRequest;
import com.example.demo.dto.watchhistory.WatchHistoryResponse;
import com.example.demo.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watch-history")
@RequiredArgsConstructor
public class WatchHistoryController {
    private final WatchHistoryService watchHistoryService;

    // 관람기록 등록
    @PostMapping
    public ResponseEntity<WatchHistoryResponse> create(
            @RequestParam Long userId,  // 임시로 파라미터로 받음 (나중에 JWT에서 추출)
            @RequestBody CreateWatchHistoryRequest request) {
        WatchHistoryResponse response = watchHistoryService.create(userId, request);
        return ResponseEntity.ok(response);
    }

    // 내 관람기록 조회
    @GetMapping("/my")
    public ResponseEntity<List<WatchHistoryResponse>> getMyHistory(
            @RequestParam Long userId) {  // 임시로 파라미터로 받음
        List<WatchHistoryResponse> responses = watchHistoryService.getMyHistory(userId);
        return ResponseEntity.ok(responses);
    }

    // 관람기록 삭제
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> delete(
            @RequestParam Long userId,  // 임시로 파라미터로 받음
            @PathVariable Long historyId) {
        watchHistoryService.delete(userId, historyId);
        return ResponseEntity.noContent().build();
    }
}
