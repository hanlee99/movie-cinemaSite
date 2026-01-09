package com.movierang.service;

import com.movierang.dto.user.RegisterRequest;
import com.movierang.dto.user.RegisterResponse;
import com.movierang.entity.UserEntity;
import com.movierang.entity.Wishlist;
import com.movierang.exception.BadRequestException;
import com.movierang.repository.MovieStatsRepository;
import com.movierang.repository.UserRepository;
import com.movierang.repository.WatchHistoryRepository;
import com.movierang.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WishlistRepository wishlistRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final MovieStatsRepository movieStatsRepository;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 엔티티 생성 (LOCAL 계정)
        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .oauthProvider("LOCAL")  // 로컬 계정 표시
                .build();

        // 4. 저장
        UserEntity savedUser = userRepository.save(user);

        // 5. 응답 반환
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname()
        );
    }

    @Transactional
    @Retryable(
        value = OptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 50)
    )
    public void deleteUser(Long userId, String password) {
        // 1. 유저 조회 및 존재 확인
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("존재하지 않는 사용자입니다."));

        // 2. LOCAL 계정인 경우 비밀번호 검증
        if (!user.isOAuthUser()) {
            if (password == null || password.isEmpty()) {
                throw new BadRequestException("비밀번호를 입력해주세요.");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadRequestException("비밀번호가 일치하지 않습니다.");
            }
        }

        log.info("회원 탈퇴 시작 - userId: {}, email: {}, provider: {}",
            userId, user.getEmail(), user.getOauthProvider());

        // 3. 찜 목록 삭제 및 MovieStats 업데이트
        deleteUserWishlists(userId);

        // 4. 관람기록 삭제
        int deletedHistoryCount = watchHistoryRepository.deleteAllByUserId(userId);
        log.info("관람기록 삭제 완료 - userId: {}, count: {}", userId, deletedHistoryCount);

        // 5. 유저 엔티티 삭제
        userRepository.delete(user);
        log.info("회원 탈퇴 완료 - userId: {}, email: {}", userId, user.getEmail());
    }

    private void deleteUserWishlists(Long userId) {
        // 1. 찜 목록 조회 (MovieStats 업데이트를 위해)
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);

        if (wishlists.isEmpty()) {
            log.info("삭제할 찜 목록 없음 - userId: {}", userId);
            return;
        }

        // 2. 영화별로 그룹화 (같은 영화를 여러 번 찜할 수 없으므로 1:1 매핑)
        Map<Long, Long> movieIdToWishlistCountMap = wishlists.stream()
            .collect(Collectors.groupingBy(
                Wishlist::getMovieId,
                Collectors.counting()
            ));

        log.info("찜 삭제 대상 - userId: {}, wishlistCount: {}, movieCount: {}",
            userId, wishlists.size(), movieIdToWishlistCountMap.size());

        // 3. MovieStats 일괄 업데이트
        for (Map.Entry<Long, Long> entry : movieIdToWishlistCountMap.entrySet()) {
            Long movieId = entry.getKey();

            movieStatsRepository.findById(movieId).ifPresent(stats -> {
                stats.decrementWishlist();
                movieStatsRepository.save(stats);
                log.debug("MovieStats 업데이트 - movieId: {}, wishlistCount: {}",
                    movieId, stats.getWishlistCount());
            });
        }

        // 4. 찜 목록 일괄 삭제
        int deletedWishlistCount = wishlistRepository.deleteAllByUserId(userId);
        log.info("찜 목록 삭제 완료 - userId: {}, count: {}", userId, deletedWishlistCount);
    }
}
