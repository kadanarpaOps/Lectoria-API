package com.lectoria_api.auth.infrastructure.output.rest.dto.in;

import feign.form.FormProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.lectoria_api.auth.domain.constants.Constants.CLIENT_ID;
import static com.lectoria_api.auth.domain.constants.Constants.CLIENT_SECRET;
import static com.lectoria_api.auth.domain.constants.Constants.GRANT_TYPE;

@Builder
@Getter @Setter
public class KeycloakLoginFormDTO {

    @FormProperty(GRANT_TYPE)
    private String grantType;
    @FormProperty(CLIENT_ID)
    private String clientId;
    @FormProperty(CLIENT_SECRET)
    private String clientSecret;
    private String username;
    private String password;
    private String scope;

}
