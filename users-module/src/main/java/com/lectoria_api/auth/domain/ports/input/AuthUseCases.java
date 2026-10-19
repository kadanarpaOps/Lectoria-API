package com.lectoria_api.auth.domain.ports.input;

import com.lectoria_api.auth.domain.model.LoginRequest;
import com.lectoria_api.auth.domain.model.LoginResponse;
import com.lectoria_api.auth.domain.model.RefreshAccessResponse;
import com.lectoria_api.auth.domain.model.TokenValidationResponse;
import com.lectoria_api.users.domain.ports.input.SharedAuthUseCases;

public interface AuthUseCases extends SharedAuthUseCases {

    LoginResponse login(LoginRequest loginRequest);
    void logout(String refreshToken);
    TokenValidationResponse validateToken(String token);
    RefreshAccessResponse refreshAccessToken(String refreshToken);

}
