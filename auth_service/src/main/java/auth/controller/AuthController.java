package auth.controller;

import auth.exception.InvalidTokenException;
import auth.security.JwtTokenIssuer;
import auth.service.ITokenBlacklistService;
import com.shared.dto.auth.*;
import auth.service.IAuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final JwtTokenIssuer jwtTokenIssuer;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ITokenBlacklistService tokenBlacklistService;


    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> processRegister(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        kafkaTemplate.send("user-registration-topic", response.username());

        return ResponseEntity.status(HttpStatus.CREATED).body(issueTokenPair(response));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> processLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(issueTokenPair(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        Claims claims = parseAndValidateRefreshToken(request.refreshToken());
        UUID userId = extractUserId(claims);

        // Роли читаем заново из БД — если доступ отозван, это применится немедленно
        AuthResponse userData = authService.getUserForRefresh(userId);

        // Ротация: старый refresh-токен сразу в blacklist, чтобы его нельзя было переиспользовать повторно
        blacklist(request.refreshToken(), userId, claims.getExpiration(), "ROTATED");

        return ResponseEntity.ok(issueTokenPair(userData));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        Claims claims;
        try {
            claims = jwtTokenIssuer.parseClaims(request.refreshToken());
        } catch (InvalidTokenException | IllegalArgumentException e) {
            return ResponseEntity.noContent().build();
        }

        UUID userId = extractUserId(claims);
        if (!tokenBlacklistService.isTokenBlacklisted(request.refreshToken())) {
            blacklist(request.refreshToken(), userId, claims.getExpiration(), "LOGOUT");
        }
        return ResponseEntity.noContent().build();
    }

    // ---------- private helpers ----------

    private Claims parseAndValidateRefreshToken(String token) {
        try {
            Claims claims = jwtTokenIssuer.parseClaims(token);
            if (!"refresh".equals(claims.get("type", String.class))) {
                throw new InvalidTokenException("Переданный токен не является refresh-токеном");
            }
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                throw new InvalidTokenException("Refresh-токен отозван");
            }
            return claims;
        } catch (InvalidTokenException | IllegalArgumentException e) {
            throw new InvalidTokenException("Невалидный или просроченный refresh-токен");
        }
    }

    private UUID extractUserId(Claims claims) {
        try {
            return UUID.fromString(claims.get("userId", String.class));
        } catch (Exception e) {
            throw new InvalidTokenException("Повреждённый refresh-токен: отсутствует userId");
        }
    }

    private void blacklist(String token, UUID userId, Date expiration, String reason) {
        LocalDateTime expiresAt = expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        tokenBlacklistService.blacklistToken(
                new CreateBlacklistTokenRequest(reason, token, LocalDateTime.now(), userId, expiresAt));
    }

    private JwtAuthResponse issueTokenPair(AuthResponse response) {
        String accessToken = jwtTokenIssuer.createAccessToken(response.userId(), response.username(), response.roles());
        String refreshToken = jwtTokenIssuer.createRefreshToken(response.userId(), response.username());
        return new JwtAuthResponse(accessToken, refreshToken);
    }
}