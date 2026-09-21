package com.lectoria_api.auth.application.service;

import com.lectoria_api.auth.domain.exceptions.auth.InvalidRefreshAccessAttemptException;
import com.lectoria_api.auth.domain.exceptions.auth.NoAuthenticationFoundExcepcion;
import com.lectoria_api.auth.domain.model.LoginRequest;
import com.lectoria_api.auth.domain.model.LoginResponse;
import com.lectoria_api.auth.domain.model.RefreshAccessResponse;
import com.lectoria_api.auth.domain.model.TokenValidationResponse;
import com.lectoria_api.auth.domain.ports.input.AuthUseCases;
import com.lectoria_api.auth.domain.ports.output.AuthConnectorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.lectoria_api.auth.domain.constants.Constants.SUB;

@RequiredArgsConstructor
@Service
public class AuthServicePort implements AuthUseCases {

    private final AuthConnectorPort authConnector;

    /**
     * This method takes care of the User Logging in process, this means the request to
     * the External Service <strong>Keycloak</strong> to validate the user credentials
     * and ask for an <code>accessToken</code> and <code>refreshToken</code>
     * @param loginRequest
     * @return A data object that contains both the <code>accessToken</code> and <code>refreshToken</code>
     * with its lifespans
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = authConnector.login(loginRequest);
        return loginResponse;
    }

    /**
     * This method takes care of the User logging out process, this means the request to
     * the External Service <strong>Keycloak</strong> to automatically invalidate both the
     * <code>accessToken</code> and <code>refreshToken</code>
     * @param refreshToken the Http Only Cookie Value
     */
    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null) return;
        authConnector.logout(refreshToken);
    }

    /**
     * It validated both the <code>accessToken</code> and <code>refreshToken</code> status
     * against the External Service <strong>Keycloak</strong>
     * @param token the Http Only Cookie Value
     * @return A data object that contains the token status and token claim <code>sub</code>
     */
    @Override
    public TokenValidationResponse validateToken(String token) {
        if (token == null) return TokenValidationResponse.builder().active(false).build();
        TokenValidationResponse validationResponse = authConnector.validateToken(token);
        return validationResponse;
    }

    /**
     * This method is responsible for the Authenticated User Access refresh, this means
     * the connection with the <strong>Keycloak</strong> External Service receiving the actual <code>refreshToken</code>
     * HTTP Only Cookie from the Client Side, verifying if there is one and if it is valid
     * @param refreshToken the Http Only Cookie Value
     * @return The new access token with its respective lifespan
     */
    @Override
    public RefreshAccessResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null) throw new InvalidRefreshAccessAttemptException();

        RefreshAccessResponse refreshAccessResponse = authConnector.refreshAccess(refreshToken);
        return refreshAccessResponse;
    }

    /**
     * This method ensures the authenticated user ID recovery, using the Spring Security
     * Context Holder to extract the Token Stored Claims, returning the <code>sub</code> one,
     * that is the Authenticated User ID
     * @return The Authenticated User ID is there is one
     */
    @Override
    public UUID getAuthUserId() {
        Authentication actualAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if (actualAuthentication == null || actualAuthentication.getPrincipal() == null)
            throw new NoAuthenticationFoundExcepcion();

        Jwt tokenCredentials = (Jwt) actualAuthentication.getCredentials();
        Map<String, Object> claims = tokenCredentials.getClaims();
        UUID userId = UUID.fromString((String) claims.get(SUB));

        return userId;
    }

}
