package com.lectoria_api.auth.infrastructure.output.rest.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.lectoria_api.auth.domain.constants.Constants.ACCESS_EXPIRES_IN;
import static com.lectoria_api.auth.domain.constants.Constants.ACCESS_TOKEN;
import static com.lectoria_api.auth.domain.constants.Constants.REFRESH_EXPIRES_IN;
import static com.lectoria_api.auth.domain.constants.Constants.REFRESH_TOKEN;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class KeycloakLoginResponse {

    @JsonProperty(ACCESS_TOKEN)
    private String accessToken;
    @JsonProperty(REFRESH_TOKEN)
    private String refreshToken;
    @JsonProperty(ACCESS_EXPIRES_IN)
    private Long accessDuration;
    @JsonProperty(REFRESH_EXPIRES_IN)
    private Long refreshDuration;

}
