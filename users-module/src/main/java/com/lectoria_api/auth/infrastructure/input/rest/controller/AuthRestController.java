package com.lectoria_api.auth.infrastructure.input.rest.controller;

import com.lectoria_api.auth.domain.model.LoginRequest;
import com.lectoria_api.auth.domain.model.LoginResponse;
import com.lectoria_api.auth.domain.model.RefreshAccessResponse;
import com.lectoria_api.auth.domain.model.TokenValidationResponse;
import com.lectoria_api.auth.domain.ports.input.AuthUseCases;
import com.lectoria_api.auth.infrastructure.input.rest.dto.in.LoginRequestDTO;
import com.lectoria_api.auth.infrastructure.input.rest.dto.out.ValidationResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lectoria_api.auth.domain.constants.Constants.ACCESS_TOKEN;
import static com.lectoria_api.auth.domain.constants.Constants.BLANK;
import static com.lectoria_api.auth.domain.constants.Constants.COOKIE_PATH;
import static com.lectoria_api.auth.domain.constants.Constants.COOKIE_SECURE_DEV;
import static com.lectoria_api.auth.domain.constants.Constants.HTTP_ONLY;
import static com.lectoria_api.auth.domain.constants.Constants.REFRESH_TOKEN;
import static com.lectoria_api.auth.domain.constants.Constants.SAME_SITE_DEV;
import static com.lectoria_api.auth.domain.constants.Constants.ZERO;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthUseCases authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.login(
                LoginRequest.builder()
                        .principal(loginRequest.getPrincipal())
                        .password(loginRequest.getPassword())
                        .build()
        );

        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(ACCESS_TOKEN, loginResponse.getAccessToken(), loginResponse.getAccessDuration())
        );
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(REFRESH_TOKEN, loginResponse.getRefreshToken(), loginResponse.getRefreshDuration())
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(ACCESS_TOKEN, BLANK, ZERO)
        );
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(REFRESH_TOKEN, BLANK, ZERO)
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate/access")
    public ResponseEntity<ValidationResponseDTO> validateAccess(
            @CookieValue(name = ACCESS_TOKEN, required = false) String accessToken
    ) {
        TokenValidationResponse validationResponse = authService.validateToken(accessToken);

        return ResponseEntity.ok().body(
                ValidationResponseDTO.builder()
                        .active(validationResponse.isActive())
                        .build()
        );
    }

    @PostMapping("/validate/session")
    public ResponseEntity<ValidationResponseDTO> validateSession(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken
    ) {
        TokenValidationResponse validationResponse = authService.validateToken(refreshToken);

        return ResponseEntity.ok().body(
                ValidationResponseDTO.builder()
                        .active(validationResponse.isActive())
                        .build()
        );
    }

    @PostMapping("/refresh-access")
    public ResponseEntity<Void> refreshAccess(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        RefreshAccessResponse refreshResponse = authService.refreshAccessToken(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(ACCESS_TOKEN, refreshResponse.getAccessToken(), refreshResponse.getAccessDuration())
        );

        return ResponseEntity.ok().build();
    }

    private String buildCookie(String cookieName, String cookieValue, Long cookieTime) {
        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(HTTP_ONLY)
                .sameSite(SAME_SITE_DEV)
                .secure(COOKIE_SECURE_DEV)
                .path(COOKIE_PATH)
                .maxAge(cookieTime)
                .build().toString();
    }

}
