package com.example.demo.controller.api;

import com.example.demo.dto.watchhistory.CreateWatchHistoryRequest;
import com.example.demo.dto.watchhistory.WatchHistoryResponse;
import com.example.demo.security.CustomOAuth2User;
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
            @AuthenticationPrincipal Object principal,
            @RequestBody CreateWatchHistoryRequest request) {
        Long userId = getUserId(principal);
        WatchHistoryResponse response = watchHistoryService.create(userId, request);
        return ResponseEntity.ok(response);
    }

    // 내 관람기록 조회
    @GetMapping("/my")
    public ResponseEntity<List<WatchHistoryResponse>> getMyHistory(
            @AuthenticationPrincipal Object principal) {
        Long userId = getUserId(principal);
        List<WatchHistoryResponse> responses = watchHistoryService.getMyHistory(userId);
        return ResponseEntity.ok(responses);
    }

    // 관람기록 삭제
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Object principal,
            @PathVariable Long historyId) {
        Long userId = getUserId(principal);
        watchHistoryService.delete(userId, historyId);
        return ResponseEntity.noContent().build();
    }

    // OAuth와 Form 로그인 모두 지원하는 헬퍼 메서드
    private Long getUserId(Object principal) {
        if (principal instanceof CustomOAuth2User oauth2User) {
            return oauth2User.getUserId();
        } else if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }
}
