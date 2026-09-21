package com.lectoria_api.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginRequest {

    private String principal;
    private String password;

}
