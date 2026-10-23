package auth.service;
import com.shared.dto.auth.AuthResponse;
import com.shared.dto.auth.LoginRequest;
import com.shared.dto.auth.RegisterRequest;

import java.util.UUID;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse getUserForRefresh(UUID userId);
}