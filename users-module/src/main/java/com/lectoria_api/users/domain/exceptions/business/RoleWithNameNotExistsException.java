package com.lectoria_api.users.domain.exceptions.business;

import com.lectoria_api.users.domain.constants.Constants;

public class RoleWithNameNotExistsException extends RuntimeException {
    public RoleWithNameNotExistsException(String roleName) {
        super(
                String.format(Constants.ROLE_WITH_NAME_NOT_EXISTS_MESSAGE, roleName)
        );
    }
}
