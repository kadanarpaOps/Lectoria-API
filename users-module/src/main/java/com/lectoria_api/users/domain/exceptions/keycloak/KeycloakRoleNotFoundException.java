package com.lectoria_api.users.domain.exceptions.keycloak;

import com.lectoria_api.users.domain.constants.Constants;

public class KeycloakRoleNotFoundException extends RuntimeException {
    public KeycloakRoleNotFoundException(String roleName) {
        super(
                String.format(Constants.ROLE_NOT_FOUND_IN_KEYCLOAK_MESSAGE, roleName)
        );
    }
}
