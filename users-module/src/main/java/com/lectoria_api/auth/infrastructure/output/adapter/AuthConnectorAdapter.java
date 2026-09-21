package com.lectoria_api.auth.infrastructure.output.adapter;

import com.lectoria_api.auth.domain.exceptions.auth.IncorrectCredentialsException;
import com.lectoria_api.auth.domain.model.LoginRequest;
import com.lectoria_api.auth.domain.model.LoginResponse;
import com.lectoria_api.auth.domain.model.RefreshAccessResponse;
import com.lectoria_api.auth.domain.model.TokenValidationResponse;
import com.lectoria_api.auth.domain.ports.output.AuthConnectorPort;
import com.lectoria_api.auth.infrastructure.output.client.KeycloakAuthClient;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakIntrospectFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakLoginFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakLogoutFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakRefreshFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.out.KeycloakIntrospectResponse;
import com.lectoria_api.auth.infrastructure.output.rest.dto.out.KeycloakLoginResponse;
import com.lectoria_api.auth.infrastructure.output.rest.dto.out.KeycloakRefreshResponse;
import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.lectoria_api.auth.domain.constants.Constants.AUTH_OP;
import static com.lectoria_api.auth.domain.constants.Constants.INTR_OP;
import static com.lectoria_api.auth.domain.constants.Constants.LOGT_OP;
import static com.lectoria_api.auth.domain.constants.Constants.PASSWORD;
import static com.lectoria_api.auth.domain.constants.Constants.REFRESH_TOKEN;
import static com.lectoria_api.auth.domain.constants.Constants.REFR_OP;
import static com.lectoria_api.auth.domain.constants.Constants.SCOPE_OPENID;
import static com.lectoria_api.users.domain.constants.Constants.INVALID_TOKEN;
import static com.lectoria_api.users.domain.constants.Constants.KEYCLOAK_ERR_CONNECTION;

@RequiredArgsConstructor
@Component
public class AuthConnectorAdapter implements AuthConnectorPort {

    private final KeycloakAuthClient authClient;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        KeycloakLoginFormDTO authData = KeycloakLoginFormDTO.builder()
                .grantType(PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(loginRequest.getPrincipal())
                .password(loginRequest.getPassword())
                .scope(SCOPE_OPENID)
                .build();

        KeycloakLoginResponse loginResponseData;

        try {
            loginResponseData = authClient.login(authData).getBody();
        } catch (FeignException e) {
            if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                throw new IncorrectCredentialsException();
            } else {
                throw new FailedOperationException(AUTH_OP, KEYCLOAK_ERR_CONNECTION);
            }
        }

        return LoginResponse.builder()
                .accessToken(loginResponseData.getAccessToken())
                .refreshToken(loginResponseData.getRefreshToken())
                .accessDuration(loginResponseData.getAccessDuration())
                .refreshDuration(loginResponseData.getRefreshDuration())
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        KeycloakLogoutFormDTO logoutData = KeycloakLogoutFormDTO.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .refreshToken(refreshToken)
                .build();
        try {
            authClient.logout(logoutData);
        } catch (Exception e) {
            throw new FailedOperationException(LOGT_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        KeycloakIntrospectFormDTO introspectData = KeycloakIntrospectFormDTO.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .token(token)
                .build();

        KeycloakIntrospectResponse introspectResponse;

        try {
            introspectResponse = authClient.introspect(introspectData).getBody();
        } catch (Exception e) {
            throw new FailedOperationException(INTR_OP, KEYCLOAK_ERR_CONNECTION);
        }

        return TokenValidationResponse.builder()
                .active((boolean) introspectResponse.getActive())
                .userId(introspectResponse.getSub())
                .build();
    }

    @Override
    public RefreshAccessResponse refreshAccess(String refreshToken) {
        KeycloakRefreshFormDTO refreshData = KeycloakRefreshFormDTO.builder()
                .grantType(REFRESH_TOKEN)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .refreshToken(refreshToken)
                .build();

        KeycloakRefreshResponse refreshResponse;

        try {
            refreshResponse = authClient.refresh(refreshData).getBody();
        } catch (Exception e) {
            if (e instanceof FeignException.BadRequest)
                throw new FailedOperationException(REFR_OP, INVALID_TOKEN);
            else
                throw new FailedOperationException(REFR_OP, KEYCLOAK_ERR_CONNECTION);
        }

        return RefreshAccessResponse.builder()
                .accessToken(refreshResponse.getAccessToken())
                .accessDuration(refreshResponse.getAccessDuration())
                .build();
    }

}
