package com.movierang.oauth;

import com.movierang.entity.UserEntity;
import com.movierang.entity.UserRole;
import com.movierang.repository.UserRepository;
import com.movierang.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "naver" or "google"
        String attributeKey = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // Provider별로 사용자 정보 파싱
        Map<String, Object> attributes = oauth2User.getAttributes();
        OAuth2UserInfo userInfo = getOAuth2UserInfo(provider, attributes);

        log.info("OAuth2 로그인 시도 - Provider: {}, Email: {}", userInfo.getProvider(), userInfo.getEmail());

        // DB에서 사용자 찾거나 생성
        UserEntity user = saveOrUpdate(userInfo);

        // Spring Security가 사용할 Principal 반환
        return new CustomOAuth2User(user, attributes, attributeKey);
    }

    private OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "naver" -> new NaverOAuth2UserInfo(attributes);
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth provider: " + provider);
        };
    }

    private UserEntity saveOrUpdate(OAuth2UserInfo userInfo) {
        // 1. 먼저 같은 Provider + OAuthId로 찾기 (동일한 OAuth 계정)
        return userRepository.findByOauthProviderAndOauthId(
                userInfo.getProvider(),
                userInfo.getOauthId()
        ).map(user -> {
            // 기존 OAuth 유저 정보 업데이트
            user.setNickname(userInfo.getNickname());
            user.setEmail(userInfo.getEmail());
            log.info("기존 OAuth 유저 업데이트 - ID: {}, Email: {}, Provider: {}",
                user.getId(), user.getEmail(), user.getOauthProvider());
            return userRepository.save(user);
        }).orElseGet(() -> {
            // 2. 같은 이메일로 기존 계정이 있는지 확인 (계정 통합)
            return userRepository.findByEmail(userInfo.getEmail())
                .map(existingUser -> {
                    // 기존 계정에 새로운 OAuth 정보 추가 (첫 번째 OAuth 정보만 유지)
                    if (existingUser.getOauthProvider() == null || existingUser.getOauthProvider().equals("LOCAL")) {
                        // LOCAL 계정이거나 OAuth 정보가 없으면 업데이트
                        existingUser.setOauthProvider(userInfo.getProvider());
                        existingUser.setOauthId(userInfo.getOauthId());
                        log.info("기존 계정에 OAuth 정보 추가 - ID: {}, Email: {}, New Provider: {}",
                            existingUser.getId(), existingUser.getEmail(), userInfo.getProvider());
                    } else {
                        // 이미 다른 OAuth로 연동된 계정
                        log.warn("이메일 중복 - 다른 Provider로 이미 가입됨. Email: {}, 기존: {}, 시도: {}",
                            userInfo.getEmail(), existingUser.getOauthProvider(), userInfo.getProvider());
                    }
                    // 닉네임은 최신 정보로 업데이트
                    existingUser.setNickname(userInfo.getNickname());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // 3. 완전히 새로운 유저 생성
                    UserEntity newUser = UserEntity.builder()
                            .email(userInfo.getEmail())
                            .nickname(userInfo.getNickname())
                            .oauthProvider(userInfo.getProvider())
                            .oauthId(userInfo.getOauthId())
                            .role(UserRole.USER)
                            .build();
                    log.info("신규 OAuth 유저 생성 - Email: {}, Provider: {}",
                        newUser.getEmail(), newUser.getOauthProvider());
                    return userRepository.save(newUser);
                });
        });
    }
}
