package com.chatapp.service;

import com.chatapp.data.entity.RefreshToken;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.RefreshTokenRepository;
import com.chatapp.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUserId(user.getId());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusSeconds(refreshTokenExpiration)
        );
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidTokenException("Invalid refresh token."));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new InvalidTokenException("Refresh token has been revoked.");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token has expired.");
        }

        return refreshToken;
    }

    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidTokenException("Invalid refresh token."));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}