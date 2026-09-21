package com.lectoria_api.users.domain.exceptions.business;

import static com.lectoria_api.users.domain.constants.Constants.USER_ROLES_LIST_CANT_BE_EMPTY_MESSAGE;

public class UserRolesListCantBeEmptyException extends RuntimeException {
    public UserRolesListCantBeEmptyException() {
        super(USER_ROLES_LIST_CANT_BE_EMPTY_MESSAGE);
    }
}
