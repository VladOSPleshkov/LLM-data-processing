package com.shared.service;
import com.shared.dto.auth.AuthRequest;
import com.shared.dto.auth.AuthResponse;

public interface IAuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse login(AuthRequest request);
}