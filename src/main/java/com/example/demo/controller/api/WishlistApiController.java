package com.example.demo.controller.api;

import com.example.demo.security.CustomOAuth2User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Wishlist API", description = "찜 관련 API (내부 데이터 수집용)")
@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistApiController {

    private final WishlistService wishlistService;

    @PostMapping("/{movieId}")
    public ResponseEntity<Map<String, Object>> toggleWishlist(
            @PathVariable Long movieId,
            @AuthenticationPrincipal Object principal
    ) {
        Long userId = getUserId(principal);
        boolean isWishlisted = wishlistService.toggleWishlist(userId, movieId);

        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "isWishlisted", isWishlisted,
                "message", isWishlisted ? "찜 목록에 추가되었습니다." : "찜 목록에서 제거되었습니다."
        ));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Map<String, Object>> checkWishlist(
            @PathVariable Long movieId,
            @AuthenticationPrincipal Object principal
    ) {
        Long userId = getUserId(principal);
        boolean isWishlisted = wishlistService.isWishlisted(userId, movieId);

        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "isWishlisted", isWishlisted
        ));
    }

    @Operation(summary = "내 찜 목록 조회", description = "로그인한 사용자의 찜 목록 조회")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyWishlist(
            @AuthenticationPrincipal Object principal
    ) {
        Long userId = getUserId(principal);
        var wishlist = wishlistService.getMyWishlist(userId);

        return ResponseEntity.ok(Map.of(
                "wishlist", wishlist
        ));
    }


    private Long getUserId(Object principal) {
        if (principal instanceof CustomOAuth2User oauth2User) {
            return oauth2User.getUserId();
        } else if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }
}
