package com.example.demo.service;

import com.example.demo.dto.watchhistory.CreateWatchHistoryRequest;
import com.example.demo.dto.watchhistory.UpdateWatchHistoryRequest;
import com.example.demo.dto.watchhistory.WatchHistoryResponse;
import com.example.demo.entity.MovieEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.WatchHistory;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchHistoryService {
    private final WatchHistoryRepository watchHistoryRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    // 관람기록 등록
    @Transactional
    public WatchHistoryResponse create(Long userId, CreateWatchHistoryRequest request) {
        // 1. 유저 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 영화 정보 조회 (제목과 포스터를 위해)
        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElse(null);

        // 3. 엔티티 생성
        WatchHistory watchHistory = WatchHistory.builder()
                .user(user)
                .movieId(request.getMovieId())
                .watchedAt(request.getWatchedAt())
                .cinemaId(request.getCinemaId())
                .cinemaName(request.getCinemaName())
                .showTime(request.getShowTime())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        // 4. 저장
        WatchHistory saved = watchHistoryRepository.save(watchHistory);

        // 5. 응답 반환 (영화 정보 포함)
        return WatchHistoryResponse.from(saved, movie);
    }

    // 내 관람기록 조회 (영화 정보 포함)
    public List<WatchHistoryResponse> getMyHistory(Long userId) {
        // 1. 관람기록 조회
        List<WatchHistory> histories = watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(userId);

        if (histories.isEmpty()) {
            return List.of();
        }

        // 2. 영화 ID 목록 추출
        List<Long> movieIds = histories.stream()
                .map(WatchHistory::getMovieId)
                .distinct()
                .toList();

        // 3. 영화 정보 일괄 조회 (N+1 문제 해결)
        Map<Long, MovieEntity> movieMap = movieRepository.findAllById(movieIds).stream()
                .collect(Collectors.toMap(MovieEntity::getId, movie -> movie));

        // 4. Response 생성 (영화 정보 포함)
        return histories.stream()
                .map(history -> {
                    MovieEntity movie = movieMap.get(history.getMovieId());
                    return WatchHistoryResponse.from(history, movie);
                })
                .toList();
    }

    // 관람기록 수정
    @Transactional
    public WatchHistoryResponse update(Long userId, Long historyId, UpdateWatchHistoryRequest request) {
        // 1. 관람기록 조회
        WatchHistory watchHistory = watchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관람기록입니다."));

        // 2. 본인 확인
        if (!watchHistory.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 관람기록만 수정할 수 있습니다.");
        }

        // 3. 업데이트
        watchHistory.update(
                request.getWatchedAt(),
                request.getCinemaId(),
                request.getCinemaName(),
                request.getShowTime(),
                request.getRating(),
                request.getComment()
        );

        // 4. 영화 정보 조회
        MovieEntity movie = movieRepository.findById(watchHistory.getMovieId())
                .orElse(null);

        // 5. 응답 반환
        return WatchHistoryResponse.from(watchHistory, movie);
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
