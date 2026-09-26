package com.lectoria_api.auth.infrastructure.input.rest.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class LoginRequestDTO {

    private String principal;
    private String password;

}
