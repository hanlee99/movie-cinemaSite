package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 찜 목록 엔티티
 *
 * 사용자가 영화를 찜한 관계를 저장
 * 같은 유저가 같은 영화를 중복으로 찜할 수 없도록 유니크 제약 설정
 */
@Entity
@Table(
    name = "wishlist",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_wishlist_user_movie",
        columnNames = {"user_id", "movie_id"}
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
