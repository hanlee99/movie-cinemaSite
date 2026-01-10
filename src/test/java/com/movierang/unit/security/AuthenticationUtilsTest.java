package com.movierang.unit.security;

import com.movierang.entity.UserEntity;
import com.movierang.entity.UserRole;
import com.movierang.exception.UnauthorizedException;
import com.movierang.security.AuthenticationUtils;
import com.movierang.security.CustomOAuth2User;
import com.movierang.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AuthenticationUtils 단위 테스트
 * - OAuth2, Form 로그인 모두 지원 확인
 * - 엣지 케이스 (null, 잘못된 타입) 처리 확인
 */
@DisplayName("AuthenticationUtils 단위 테스트")
class AuthenticationUtilsTest {

    @Nested
    @DisplayName("getUserId 메서드")
    class GetUserId {

        @Test
        @DisplayName("OAuth2 로그인 사용자 - userId 반환")
        void OAuth2_사용자_ID_추출() {
            // given
            UserEntity user = createTestUser(1L, "test@google.com", "테스터");
            CustomOAuth2User oauth2User = new CustomOAuth2User(
                    user,
                    Map.of("sub", "google-oauth-id"),
                    "sub"
            );

            // when
            Long userId = AuthenticationUtils.getUserId(oauth2User);

            // then
            assertThat(userId).isEqualTo(1L);
        }

        @Test
        @DisplayName("Form 로그인 사용자 - userId 반환")
        void Form_로그인_사용자_ID_추출() {
            // given
            UserEntity user = createTestUser(2L, "test@local.com", "로컬유저");
            CustomUserDetails userDetails = new CustomUserDetails(user);

            // when
            Long userId = AuthenticationUtils.getUserId(userDetails);

            // then
            assertThat(userId).isEqualTo(2L);
        }

        @Test
        @DisplayName("null principal - UnauthorizedException 발생")
        void null_principal_예외() {
            // when & then
            assertThatThrownBy(() -> AuthenticationUtils.getUserId(null))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("인증되지 않은 사용자");
        }

        @Test
        @DisplayName("잘못된 타입의 principal - UnauthorizedException 발생")
        void 잘못된_타입_예외() {
            // given
            String invalidPrincipal = "anonymous";

            // when & then
            assertThatThrownBy(() -> AuthenticationUtils.getUserId(invalidPrincipal))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("인증되지 않은 사용자");
        }

        @Test
        @DisplayName("다양한 OAuth Provider 테스트 - Google")
        void Google_OAuth_사용자() {
            // given
            UserEntity user = UserEntity.builder()
                    .id(10L)
                    .email("user@gmail.com")
                    .nickname("구글유저")
                    .oauthProvider("GOOGLE")
                    .oauthId("google-123")
                    .role(UserRole.USER)
                    .build();

            CustomOAuth2User oauth2User = new CustomOAuth2User(
                    user,
                    Map.of("sub", "google-123"),
                    "sub"
            );

            // when
            Long userId = AuthenticationUtils.getUserId(oauth2User);

            // then
            assertThat(userId).isEqualTo(10L);
        }

        @Test
        @DisplayName("다양한 OAuth Provider 테스트 - Naver")
        void Naver_OAuth_사용자() {
            // given
            UserEntity user = UserEntity.builder()
                    .id(20L)
                    .email("user@naver.com")
                    .nickname("네이버유저")
                    .oauthProvider("NAVER")
                    .oauthId("naver-456")
                    .role(UserRole.USER)
                    .build();

            CustomOAuth2User oauth2User = new CustomOAuth2User(
                    user,
                    Map.of("id", "naver-456"),
                    "id"
            );

            // when
            Long userId = AuthenticationUtils.getUserId(oauth2User);

            // then
            assertThat(userId).isEqualTo(20L);
        }

        @Test
        @DisplayName("관리자 사용자 - userId 반환")
        void 관리자_사용자() {
            // given
            UserEntity adminUser = UserEntity.builder()
                    .id(99L)
                    .email("admin@movierang.com")
                    .nickname("관리자")
                    .password("encoded-password")
                    .oauthProvider("LOCAL")
                    .role(UserRole.ADMIN)
                    .build();

            CustomUserDetails userDetails = new CustomUserDetails(adminUser);

            // when
            Long userId = AuthenticationUtils.getUserId(userDetails);

            // then
            assertThat(userId).isEqualTo(99L);
        }
    }

    // 테스트 헬퍼 메서드
    private UserEntity createTestUser(Long id, String email, String nickname) {
        return UserEntity.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .role(UserRole.USER)
                .build();
    }
}
