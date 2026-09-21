package com.lectoria_api.auth.infrastructure.output.rest.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class KeycloakIntrospectResponse {

    private Object active;
    private String sub;

}
