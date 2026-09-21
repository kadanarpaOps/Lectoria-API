package com.lectoria_api.users.domain.exceptions.business;

import static com.lectoria_api.users.domain.constants.Constants.USER_DONT_HAVE_THAT_ROLE_MESSAGE;

public class UserNotHaveThatRoleException extends RuntimeException {
    public UserNotHaveThatRoleException(String roleName) {
        super(
                String.format(USER_DONT_HAVE_THAT_ROLE_MESSAGE, roleName)
        );
    }
}
