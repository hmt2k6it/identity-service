package com.microservice.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.identity.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteAllByUserId(String userId);
}