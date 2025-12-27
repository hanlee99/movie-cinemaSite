package com.example.demo.controller.api;

import com.example.demo.dto.watchhistory.CreateWatchHistoryRequest;
import com.example.demo.dto.watchhistory.WatchHistoryResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watch-history")
@RequiredArgsConstructor
public class WatchHistoryApiController {
    private final WatchHistoryService watchHistoryService;

    // 관람기록 등록
    @PostMapping
    public ResponseEntity<WatchHistoryResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateWatchHistoryRequest request) {
        WatchHistoryResponse response = watchHistoryService.create(userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    // 내 관람기록 조회
    @GetMapping("/my")
    public ResponseEntity<List<WatchHistoryResponse>> getMyHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<WatchHistoryResponse> responses = watchHistoryService.getMyHistory(userDetails.getUserId());
        return ResponseEntity.ok(responses);
    }

    // 관람기록 삭제
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long historyId) {
        watchHistoryService.delete(userDetails.getUserId(), historyId);
        return ResponseEntity.noContent().build();
    }
}
