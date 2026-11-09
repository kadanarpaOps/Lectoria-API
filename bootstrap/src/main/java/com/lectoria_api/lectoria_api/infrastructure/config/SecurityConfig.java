package com.lectoria_api.lectoria_api.infrastructure.config;

import com.lectoria_api.lectoria_api.infrastructure.config.converter.KeycloakAuthConverter;
import com.lectoria_api.lectoria_api.domain.constants.Constants;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.lectoria_api.lectoria_api.domain.constants.Constants.LECTORIA_ADMIN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final KeycloakAuthConverter authConverter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/api/story/**",
                                "/api/chapter/**",
                                "/api/comment/**",
                                "/api/favorite/**",
                                "/api/genre/**",
                                "/api/rating/**",
                                "/api/tag/**",
                                "/api/view/**"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users",
                                "/api/users/forgot-password",
                                "/api/users/verify-registry",
                                "/api/auth/login",
                                "/api/auth/validate/access",
                                "/api/auth/validate/session",
                                "/api/auth/logout",
                                "/api/auth/refresh-access",
                                "/api/verifications",
                                "/api/verifications/verify"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/auth/user/activate-account",
                                "/api/verifications/regenerate-code"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/verifications/verify/verification-existence"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users",
                                "/api/auth/user/profile"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/auth/user/info",
                                "/api/auth/user/change-email",
                                "/api/auth/user/change-password",
                                "/api/auth/user/disable-account",
                                "/api/auth/user/profile-image"
                        ).authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/profile-image").authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/auth/admin/users",
                                "/api/auth/admin/find-user-by-username",
                                "/api/auth/admin/find-user-by-email",
                                "/api/notifications/templates"
                        ).hasRole(LECTORIA_ADMIN)
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/auth/admin/add-role",
                                "/api/auth/admin/remove-role",
                                "/api/auth/admin/toggle-user-status",
                                "/api/notifications/templates/{templateName}"
                        ).hasRole(LECTORIA_ADMIN)
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/notifications/send",
                                "/api/notifications/templates"
                        ).hasRole(LECTORIA_ADMIN)
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/notifications/templates/{templateId}"
                        ).hasRole(LECTORIA_ADMIN)
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(cookieAccessTokenResolver())
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwtToken ->
                                        new JwtAuthenticationToken(jwtToken, authConverter.convert(jwtToken))
                                )
                        )
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .build();
    }

    private BearerTokenResolver cookieAccessTokenResolver() {
        return request -> {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) return null;

            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(Constants.COOKIE_ACCESS_TOKEN))
                    .map(Cookie::getValue)
                    .findAny().orElse(null);
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
