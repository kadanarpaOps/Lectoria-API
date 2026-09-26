package com.lectoria_api.auth.infrastructure.output.rest.dto.in;

import feign.form.FormProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.lectoria_api.auth.domain.constants.Constants.CLIENT_ID;
import static com.lectoria_api.auth.domain.constants.Constants.CLIENT_SECRET;
import static com.lectoria_api.auth.domain.constants.Constants.TOKEN;

@Builder
@Getter @Setter
public class KeycloakIntrospectFormDTO {

    @FormProperty(CLIENT_ID)
    private String clientId;
    @FormProperty(CLIENT_SECRET)
    private String clientSecret;
    @FormProperty(TOKEN)
    private String token;

}
