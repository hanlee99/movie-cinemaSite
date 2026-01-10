package com.movierang.security;

import com.movierang.exception.UnauthorizedException;

/**
 * 인증된 사용자 정보 추출 유틸리티
 * OAuth2와 Form 로그인 모두 지원
 */
public class AuthenticationUtils {

    private AuthenticationUtils() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }

    /**
     * Principal에서 사용자 ID 추출
     * @param principal Spring Security의 @AuthenticationPrincipal
     * @return 사용자 ID
     * @throws UnauthorizedException 인증되지 않은 경우
     */
    public static Long getUserId(Object principal) {
        if (principal instanceof CustomOAuth2User oauth2User) {
            return oauth2User.getUserId();
        }
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        throw new UnauthorizedException("인증되지 않은 사용자입니다.");
    }
}
