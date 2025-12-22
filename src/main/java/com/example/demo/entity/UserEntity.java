package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일 (로그인 ID 겸용)
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    // 비밀번호 (BCrypt 암호화)
    @Column(nullable = false)
    private String password;

    // 닉네임 (화면 표시용)
    @Column(nullable = false, length = 50)
    private String nickname;

    // 인증 제공자 (LOCAL, KAKAO, NAVER 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL;

    // 권한 (USER, ADMIN)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    // 생성일 (자동)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정일 (자동)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 마지막 로그인 시각
    private LocalDateTime lastLoginAt;

    // === 비즈니스 메서드 ===

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
}
