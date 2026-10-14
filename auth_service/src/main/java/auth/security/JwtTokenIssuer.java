package auth.security;

import com.shared.security.JwtTokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenIssuer extends JwtTokenValidator {

    private final long accessValidityMs;
    private final long refreshValidityMs;

    public JwtTokenIssuer(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long accessValidityMs,
            @Value("${jwt.refresh-expiration-ms:604800000}") long refreshValidityMs) {
        super(secret);
        this.accessValidityMs = accessValidityMs;
        this.refreshValidityMs = refreshValidityMs;
    }

    /** Короткоживущий access-токен (по умолчанию 1 час), содержит роли. */
    public String createAccessToken(UUID userId, String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessValidityMs);
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId.toString())
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /**
     * Долгоживущий refresh-токен (по умолчанию 7 дней). Роли сознательно
     * НЕ кладём внутрь: если роль пользователя отозвана, это должно применяться
     * сразу на следующем /refresh (роли перечитываются из БД), а не ждать 7 дней.
     */
    public String createRefreshToken(UUID userId, String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshValidityMs);
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId.toString())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /** Верификация подписи + возврат claims. Бросает JwtException/IllegalArgumentException на невалидном токене. */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Deprecated
    public String createToken(UUID userId, String username, List<String> roles) {
        return createAccessToken(userId, username, roles);
    }
}