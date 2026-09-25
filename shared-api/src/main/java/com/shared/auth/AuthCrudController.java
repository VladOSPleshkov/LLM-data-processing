package com.shared.auth;

import com.shared.dto.auth.AuthRequest;

public interface AuthCrudController {
    String processRegister(AuthRequest request);
    String processLogin(AuthRequest request);
}
