package com.movierang.repository;

import com.movierang.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    Optional<UserEntity> findByEmailAndOauthProvider(String email, String oauthProvider);
}
