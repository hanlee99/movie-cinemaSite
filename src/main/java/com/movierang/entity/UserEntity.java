package com.movierang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 60)  // OAuth 유저는 패스워드 없음
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 20)
    private String oauthProvider;  // "NAVER", "GOOGLE", "LOCAL"

    @Column(length = 100)
    private String oauthId;  // OAuth provider의 unique ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isOAuthUser() {
        return oauthProvider != null && !oauthProvider.equals("LOCAL");
    }

    // 도메인 메서드: OAuth 정보 업데이트
    public void updateOAuthInfo(String provider, String oauthId) {
        this.oauthProvider = provider;
        this.oauthId = oauthId;
    }

    // 도메인 메서드: 닉네임 업데이트
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // 도메인 메서드: 이메일 업데이트
    public void updateEmail(String email) {
        this.email = email;
    }
}
