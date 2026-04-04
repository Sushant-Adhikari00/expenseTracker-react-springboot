package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.entity.TokenBlacklist;
import com.expensetracker.expenseTracker.repository.TokenBlacklistRepository;
import com.expensetracker.expenseTracker.security.JwtTokenProvider;
import com.expensetracker.expenseTracker.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository blacklistRepository;
    private final JwtTokenProvider         jwtTokenProvider;

    @Override
    @Transactional
    public void blacklistToken(String token) {
        String hash      = hashToken(token);
        Date   expiry    = jwtTokenProvider.getExpirationFromToken(token);
        LocalDateTime expiresAt = expiry.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Avoid duplicate entries
        if (!blacklistRepository.existsByTokenHash(hash)) {
            blacklistRepository.save(
                    TokenBlacklist.builder()
                            .tokenHash(hash)
                            .expiresAt(expiresAt)
                            .build()
            );
            log.info("Token blacklisted — expires at {}", expiresAt);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByTokenHash(hashToken(token));
    }

    // Runs every hour — removes expired tokens from DB
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cleanupExpiredTokens() {
        blacklistRepository.deleteAllExpiredBefore(LocalDateTime.now());
        log.info("Expired blacklisted tokens cleaned up");
    }

    // SHA-256 hash — never store raw tokens
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}