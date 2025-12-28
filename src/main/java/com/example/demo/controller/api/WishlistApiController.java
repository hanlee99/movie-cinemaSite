package com.example.demo.controller.api;

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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        boolean isWishlisted = wishlistService.isWishlisted(userId, movieId);

        return ResponseEntity.ok(Map.of(
                "movieId", movieId,
                "isWishlisted", isWishlisted
        ));
    }
}
