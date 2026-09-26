package com.lectoria_api.auth.domain.ports.input;

import com.lectoria_api.auth.domain.model.LoginRequest;
import com.lectoria_api.auth.domain.model.LoginResponse;
import com.lectoria_api.auth.domain.model.RefreshAccessResponse;
import com.lectoria_api.auth.domain.model.TokenValidationResponse;

import java.util.UUID;

public interface AuthUseCases {

    LoginResponse login(LoginRequest loginRequest);
    void logout(String refreshToken);
    TokenValidationResponse validateToken(String token);
    RefreshAccessResponse refreshAccessToken(String refreshToken);
    UUID getAuthUserId();

}
