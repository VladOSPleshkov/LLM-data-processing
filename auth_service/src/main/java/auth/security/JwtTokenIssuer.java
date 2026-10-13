package auth.security;

import com.shared.security.JwtTokenValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenIssuer extends JwtTokenValidator {

    private final long validityInMilliseconds;

    public JwtTokenIssuer(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long validityInMilliseconds) {
        super(secret); // Передаем ключ в родительский валидатор
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(UUID userId, String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId.toString())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key) // Доступно из родительского класса
                .compact();
    }
}