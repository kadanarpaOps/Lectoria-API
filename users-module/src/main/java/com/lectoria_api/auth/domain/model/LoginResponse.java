package com.lectoria_api.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long accessDuration;
    private Long refreshDuration;

}
