package com.lectoria_api.auth.infrastructure.output.rest.dto.in;

import feign.form.FormProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.lectoria_api.auth.domain.constants.Constants.REFRESH_TOKEN;
import static org.keycloak.OAuth2Constants.CLIENT_ID;
import static org.keycloak.OAuth2Constants.CLIENT_SECRET;

@Builder
@Getter @Setter
public class KeycloakLogoutFormDTO {

    @FormProperty(CLIENT_ID)
    private String clientId;
    @FormProperty(CLIENT_SECRET)
    private String clientSecret;
    @FormProperty(REFRESH_TOKEN)
    private String refreshToken;

}
