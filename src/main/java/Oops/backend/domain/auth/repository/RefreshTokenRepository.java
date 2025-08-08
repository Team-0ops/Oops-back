package com.example.mutsideout_mju.repository;

import com.example.mutsideout_mju.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUserId(UUID userId);
    Optional<RefreshToken> findByToken(String refreshToken);}
