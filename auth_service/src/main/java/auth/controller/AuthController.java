package auth.controller;

import auth.security.JwtTokenIssuer;
import com.shared.dto.auth.*;
import com.shared.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final JwtTokenIssuer jwtTokenIssuer;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> processRegister(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        kafkaTemplate.send("user-registration-topic", response.username());

        // Выпуск токена на основе валидных внутренних данных
        String token = jwtTokenIssuer.createToken(
                response.userId(), // Исправлено имя геттера под ваш DTO (userId вместо id)
                response.username(),
                response.roles()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtAuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> processLogin(@Valid @RequestBody LoginRequest request) {
        // Логика сервиса должна быть переписана под прием LoginRequest
        AuthResponse response = authService.login(request);

        String token = jwtTokenIssuer.createToken(
                response.userId(),
                response.username(),
                response.roles()
        );

        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    private String primaryRole(List<String> roles) {
        return (roles == null || roles.isEmpty()) ? "ROLE_MANAGER" : roles.get(0);
    }
}