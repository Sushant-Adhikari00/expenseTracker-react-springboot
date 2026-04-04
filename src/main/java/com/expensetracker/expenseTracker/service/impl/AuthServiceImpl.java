package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.dto.request.LoginRequest;
import com.expensetracker.expenseTracker.dto.request.RegisterRequest;
import com.expensetracker.expenseTracker.dto.response.AuthResponse;
import com.expensetracker.expenseTracker.entity.User;
import com.expensetracker.expenseTracker.mapper.UserMapper;
import com.expensetracker.expenseTracker.repository.UserRepository;
import com.expensetracker.expenseTracker.security.JwtTokenProvider;
import com.expensetracker.expenseTracker.service.AuthService;
import com.expensetracker.expenseTracker.service.TokenBlacklistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            tokenBlacklistService.blacklistToken(token);
           //log.info("User logged out — token blacklisted");
        }
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException(
                    "Email already registered: " + request.getEmail());
        }

        // Mapper builds entity — service only sets encoded password
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // Mapper builds response — service only attaches token
        String token = jwtTokenProvider.generateTokenFromEmail(savedUser.getEmail());
        AuthResponse response = userMapper.toAuthResponse(savedUser);
        response.setToken(token);

        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Mapper builds response — service only attaches token
        String token = jwtTokenProvider.generateToken(authentication);
        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);

        return response;
    }
}
