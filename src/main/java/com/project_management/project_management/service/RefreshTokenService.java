package com.project_management.project_management.service;

import com.project_management.project_management.model.RefreshToken;
import com.project_management.project_management.model.User;
import com.project_management.project_management.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(final RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }
    public RefreshToken createRefreshToken(User user){
       return RefreshToken.builder()
                .token(UUID.randomUUID().toString().substring(0, 8))
                .isExpired(false)
                .user(user)
                .expiresOn(LocalDateTime.now().plusDays(1))
                .build();
    }
    public void saveRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken findRefreshToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
    public void deleteRefreshToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }
}
