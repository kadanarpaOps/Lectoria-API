package com.lectoria_api.users.domain.ports.input;

import com.lectoria_api.users.domain.model.UserModel;

public interface SharedUserUseCases {

    UserModel findUserByUsernameOrEmail(String usernameOrEmail);

}
