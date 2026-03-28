package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.dto.request.LoginRequest;
import com.expensetracker.expenseTracker.dto.request.RegisterRequest;
import com.expensetracker.expenseTracker.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
