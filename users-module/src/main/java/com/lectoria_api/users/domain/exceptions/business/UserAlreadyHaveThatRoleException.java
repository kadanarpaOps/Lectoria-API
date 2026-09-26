package com.lectoria_api.users.domain.exceptions.business;

import static com.lectoria_api.users.domain.constants.Constants.USER_ALREADY_HAVE_THAT_ROLE_MESSAGE;

public class UserAlreadyHaveThatRoleException extends RuntimeException {
    public UserAlreadyHaveThatRoleException(String roleName) {
        super(
                String.format(USER_ALREADY_HAVE_THAT_ROLE_MESSAGE, roleName)
        );
    }
}
