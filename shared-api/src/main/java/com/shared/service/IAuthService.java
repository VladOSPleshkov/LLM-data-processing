package com.shared.service;
import com.shared.dto.auth.AuthResponse;
import com.shared.dto.auth.LoginRequest;
import com.shared.dto.auth.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}