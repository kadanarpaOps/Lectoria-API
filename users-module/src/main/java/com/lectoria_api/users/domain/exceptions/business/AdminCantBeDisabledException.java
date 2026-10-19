package com.lectoria_api.users.domain.exceptions.business;

import com.lectoria_api.users.domain.constants.Constants;

public class AdminCantBeDisabledException extends RuntimeException {
    public AdminCantBeDisabledException() {
        super(Constants.ADMIN_CANT_BE_DISABLED_MESSAGE);
    }
}
