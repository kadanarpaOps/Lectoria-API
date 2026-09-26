package com.lectoria_api.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RefreshAccessResponse {

    private String accessToken;
    private Long accessDuration;

}
