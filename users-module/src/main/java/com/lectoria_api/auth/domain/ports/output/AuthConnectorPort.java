package com.lectoria_api.auth.domain.ports.output;

import com.lectoria_api.auth.domain.model.LoginRequest;
import com.lectoria_api.auth.domain.model.LoginResponse;
import com.lectoria_api.auth.domain.model.RefreshAccessResponse;
import com.lectoria_api.auth.domain.model.TokenValidationResponse;

public interface AuthConnectorPort {


    LoginResponse login(LoginRequest loginRequest);
    void logout(String refreshToken);
    TokenValidationResponse validateToken(String token);
    RefreshAccessResponse refreshAccess(String refreshToken);

}
