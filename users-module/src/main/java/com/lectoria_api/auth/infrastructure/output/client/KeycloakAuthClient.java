package com.lectoria_api.auth.infrastructure.output.client;

import com.lectoria_api.auth.infrastructure.output.config.FeignAuthClientConfig;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakIntrospectFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakLoginFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakLogoutFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.in.KeycloakRefreshFormDTO;
import com.lectoria_api.auth.infrastructure.output.rest.dto.out.KeycloakIntrospectResponse;
import com.lectoria_api.auth.infrastructure.output.rest.dto.out.KeycloakLoginResponse;
import com.lectoria_api.auth.infrastructure.output.rest.dto.out.KeycloakRefreshResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "keycloak-auth", url = "${keycloak.server.url}",
        configuration = FeignAuthClientConfig.class
)
public interface KeycloakAuthClient {

    @PostMapping(
            value = "${keycloak.auth.url.login}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<KeycloakLoginResponse> login(KeycloakLoginFormDTO loginData);

    @PostMapping(
            value = "${keycloak.auth.url.logout}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<Void> logout(KeycloakLogoutFormDTO logoutData);

    @PostMapping(
            value = "${keycloak.auth.url.introspection}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<KeycloakIntrospectResponse> introspect(KeycloakIntrospectFormDTO introspectData);

    @PostMapping(
            value = "${keycloak.auth.url.login}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<KeycloakRefreshResponse> refresh(KeycloakRefreshFormDTO refreshData);

}
