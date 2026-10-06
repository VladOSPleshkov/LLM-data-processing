package com.example.controller;

import auth.dao.UserDao;
import auth.entity.User;
import com.example.security.JwtTokenProvider;
import com.shared.dto.auth.AuthRequest;
import com.shared.dto.auth.AuthResponse;
import com.shared.dto.auth.JwtAuthResponse;
import com.shared.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> processRegister(@RequestBody AuthRequest request) {
        AuthResponse response = authService.register(request);
        String token = jwtTokenProvider.createToken(response.username(), primaryRole(response.roles()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtAuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> processLogin(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        String token = jwtTokenProvider.createToken(response.username(), primaryRole(response.roles()));
        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    private String primaryRole(List<String> roles) {
        return (roles == null || roles.isEmpty()) ? "ROLE_MANAGER" : roles.get(0);
    }
}