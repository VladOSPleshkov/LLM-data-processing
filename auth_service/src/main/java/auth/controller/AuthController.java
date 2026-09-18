package auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.auth.AuthCrudController;
import com.shared.dto.auth.AuthRequest;
import com.shared.dto.auth.AuthResponse;
import com.shared.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("authController")
public class AuthController extends AbstractAuthController implements AuthCrudController {
    private final IAuthService authService;
    @Autowired
    public AuthController(IAuthService authService, ObjectMapper objectMapper) {
        super(objectMapper,"authController");
        this.authService = authService;
    }
    @Override
    public String processRegister(AuthRequest request) {
        return toJson(authService.register(request));
    }
    @Override
    public String processLogin(AuthRequest request) {
        return toJson(authService.login(request));
    }

}