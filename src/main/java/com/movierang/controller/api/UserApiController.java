package com.movierang.controller.api;

import com.movierang.entity.UserEntity;
import com.movierang.entity.Wishlist;
import com.movierang.repository.UserRepository;
import com.movierang.repository.WatchHistoryRepository;
import com.movierang.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dev/user")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {
    private final UserRepository userRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final WishlistRepository wishlistRepository;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkUserData(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();

        // 유저 존재 여부
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            result.put("exists", false);
            result.put("message", "해당 이메일의 사용자가 존재하지 않습니다.");
            return ResponseEntity.ok(result);
        }

        result.put("exists", true);
        result.put("userId", user.getId());
        result.put("email", user.getEmail());
        result.put("nickname", user.getNickname());
        result.put("oauthProvider", user.getOauthProvider());

        // 관람기록 개수
        int watchHistoryCount = watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(user.getId()).size();
        result.put("watchHistoryCount", watchHistoryCount);

        // 찜 목록 개수
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        result.put("wishlistCount", wishlists.size());

        log.info("유저 데이터 확인 - email: {}, exists: true, watchHistory: {}, wishlist: {}",
            email, watchHistoryCount, wishlists.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listAllUsers() {
        List<UserEntity> users = userRepository.findAll();

        List<Map<String, Object>> result = users.stream().map(user -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("oauthProvider", user.getOauthProvider());

            // 관람기록 및 찜 개수
            userInfo.put("watchHistoryCount",
                watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(user.getId()).size());
            userInfo.put("wishlistCount",
                wishlistRepository.findByUserId(user.getId()).size());

            return userInfo;
        }).toList();

        log.info("전체 유저 목록 조회 - count: {}", result.size());
        return ResponseEntity.ok(result);
    }
}
