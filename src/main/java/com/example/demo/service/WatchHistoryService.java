package com.example.demo.service;

import com.example.demo.dto.watchhistory.CreateWatchHistoryRequest;
import com.example.demo.dto.watchhistory.WatchHistoryResponse;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.WatchHistory;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchHistoryService {
    private final WatchHistoryRepository watchHistoryRepository;
    private final UserRepository userRepository;

    // 관람기록 등록
    @Transactional
    public WatchHistoryResponse create(Long userId, CreateWatchHistoryRequest request) {
        // 1. 유저 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 엔티티 생성
        WatchHistory watchHistory = WatchHistory.builder()
                .user(user)
                .movieId(request.getMovieId())
                .watchedAt(request.getWatchedAt())
                .comment(request.getComment())
                .build();

        // 3. 저장
        WatchHistory saved = watchHistoryRepository.save(watchHistory);

        // 4. 응답 반환
        return WatchHistoryResponse.from(saved);
    }

    // 내 관람기록 조회
    public List<WatchHistoryResponse> getMyHistory(Long userId) {
        List<WatchHistory> histories = watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(userId);

        return histories.stream()
                .map(WatchHistoryResponse::from)
                .toList();
    }

    // 관람기록 삭제
    @Transactional
    public void delete(Long userId, Long historyId) {
        WatchHistory watchHistory = watchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관람기록입니다."));

        // 본인 확인
        if (!watchHistory.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 관람기록만 삭제할 수 있습니다.");
        }

        watchHistoryRepository.delete(watchHistory);
    }
}
