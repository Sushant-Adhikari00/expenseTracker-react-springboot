package com.expensetracker.expenseTracker.controller;

import com.expensetracker.expenseTracker.dto.request.LoginRequest;
import com.expensetracker.expenseTracker.dto.request.RegisterRequest;
import com.expensetracker.expenseTracker.dto.response.AuthResponse;
import com.expensetracker.expenseTracker.exception.RateLimitException;
import com.expensetracker.expenseTracker.service.AuthService;
import com.expensetracker.expenseTracker.service.RateLimitService;
import com.expensetracker.expenseTracker.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService       authService;
    private final RateLimitService  rateLimitService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        String ip = IpUtils.getClientIp(httpRequest);

        // Check register rate limit — 3 attempts per hour per IP
        if (!rateLimitService.tryConsumeRegister(ip)) {
            long remaining = rateLimitService.getRemainingRegisterTokens(ip);
            throw new RateLimitException(
                    "Too many registration attempts. Please try again in 1 hour.",
                    3600L
            );
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ip = IpUtils.getClientIp(httpRequest);

        // Check login rate limit — 5 attempts per 15 minutes per IP
        if (!rateLimitService.tryConsumeLogin(ip)) {
            throw new RateLimitException(
                    "Too many login attempts. Please try again in 15 minutes.",
                    900L
            );
        }

        return ResponseEntity.ok(authService.login(request));
    }

    // Add this endpoint to AuthController

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader) {

        // Extract raw token from "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }

        return ResponseEntity.noContent().build();    // 204
    }
}