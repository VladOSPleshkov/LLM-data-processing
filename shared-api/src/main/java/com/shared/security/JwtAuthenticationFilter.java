package com.shared.security;

import com.shared.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

//@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenValidator jwtTokenValidator;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator, HandlerExceptionResolver resolver) {
        this.jwtTokenValidator = jwtTokenValidator;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                // Изменяем логику: валидатор теперь должен выбрасывать исключения наружу,
                // либо мы проверяем токен напрямую в фильтре
                if (jwtTokenValidator.validateToken(token)) {
                    Claims claims = jwtTokenValidator.getClaims(token);
                    String username = claims.getSubject();
                    List<?> rawRoles = claims.get("roles", List.class);

                    if (rawRoles != null) {
                        List<GrantedAuthority> authorities = rawRoles.stream()
                                .map(Object::toString)
                                .map(SimpleGrantedAuthority::new)
                                .map(GrantedAuthority.class::cast)
                                .toList();

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    // Если валидатор вернул false без выброса исключения (сломанная структура)
                    throw new InvalidTokenException("Предоставленный токен доступа не прошел валидацию подписи");
                }
            } catch (ExpiredJwtException e) {
                // Перенаправляем ошибку истекшего токена в RestControllerAdvice
                resolver.resolveException(request, response, null, new InvalidTokenException("Срок действия JWT токена истек"));
                return; // Прерываем цепочку фильтров, дальше запрос не идет!
            } catch (MalformedJwtException e) {
                resolver.resolveException(request, response, null, new InvalidTokenException("Невалидная цифровая подпись токена или структура JWT повреждена"));
                return;
            } catch (Exception e) {
                resolver.resolveException(request, response, null, new InvalidTokenException("Ошибка аутентификации: " + e.getMessage()));
                return;
            }
        }
        // Передача управления дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }
}
