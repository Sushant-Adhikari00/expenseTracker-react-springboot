package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.dto.request.LoginRequest;
import com.expensetracker.expenseTracker.dto.request.RegisterRequest;
import com.expensetracker.expenseTracker.dto.response.AuthResponse;
import com.expensetracker.expenseTracker.entity.User;
import com.expensetracker.expenseTracker.mapper.UserMapper;
import com.expensetracker.expenseTracker.repository.UserRepository;
import com.expensetracker.expenseTracker.security.JwtTokenProvider;
import com.expensetracker.expenseTracker.service.AuthService;
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

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException(
                    "Email already registered: " + request.getEmail());
        }

        // Build and save user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT
        String token = jwtTokenProvider.generateTokenFromEmail(savedUser.getEmail());

        // Map to response and attach token
        AuthResponse response = userMapper.toAuthResponse(savedUser);
        response.setToken(token);

        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Spring Security handles credential validation
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Load user for response data
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);

        return response;
    }
}
