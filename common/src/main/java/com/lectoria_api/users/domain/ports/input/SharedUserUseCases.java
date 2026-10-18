package com.lectoria_api.users.domain.ports.input;

import com.lectoria_api.users.domain.model.UserModel;

import java.util.UUID;

public interface SharedUserUseCases {

    UserModel findUserById(UUID userId);
    UserModel findAnyUserByUsername(String username, Boolean isEnabled);
    UserModel findAnyUserByUserEmail(String userEmail, Boolean isEnabled);

}
