package com.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
public class JwtTokenValidator {

    protected final SecretKey key;

    public JwtTokenValidator(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public JwtTokenValidator(SecretKey key) {
        this.key = key;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Невалидный JWT токен: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
    public String getUsername(String token) { return getClaims(token).getSubject(); }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) { return getClaims(token).get("roles", List.class); }

    public UUID getUserId(String token) {
        String id = getClaims(token).get("userId", String.class);
        return id != null ? UUID.fromString(id) : null;
    }
}