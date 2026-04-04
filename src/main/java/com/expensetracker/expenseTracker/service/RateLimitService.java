package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.config.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitProperties properties;

    // Separate bucket maps per endpoint type
    private final Map<String, Bucket> loginBuckets    = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    // Try to consume 1 token from login bucket for this IP
    public boolean tryConsumeLogin(String ipAddress) {
        return getLoginBucket(ipAddress).tryConsume(1);
    }

    // Try to consume 1 token from register bucket for this IP
    public boolean tryConsumeRegister(String ipAddress) {
        return getRegisterBucket(ipAddress).tryConsume(1);
    }

    // Get remaining tokens for login bucket
    public long getRemainingLoginTokens(String ipAddress) {
        return getLoginBucket(ipAddress)
                .getAvailableTokens();
    }

    // Get remaining tokens for register bucket
    public long getRemainingRegisterTokens(String ipAddress) {
        return getRegisterBucket(ipAddress)
                .getAvailableTokens();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Bucket getLoginBucket(String ip) {
        return loginBuckets.computeIfAbsent(ip, key ->
                buildBucket(
                        properties.getLogin().getCapacity(),
                        properties.getLogin().getRefillTokens(),
                        properties.getLogin().getRefillMinutes()
                ));
    }

    private Bucket getRegisterBucket(String ip) {
        return registerBuckets.computeIfAbsent(ip, key ->
                buildBucket(
                        properties.getRegister().getCapacity(),
                        properties.getRegister().getRefillTokens(),
                        properties.getRegister().getRefillMinutes()
                ));
    }

    private Bucket buildBucket(int capacity,
                               int refillTokens,
                               int refillMinutes) {
        Refill     refill    = Refill.greedy(
                refillTokens,
                Duration.ofMinutes(refillMinutes));
        Bandwidth  bandwidth = Bandwidth.classic(capacity, refill);
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}