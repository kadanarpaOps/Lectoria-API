package com.lectoria_api.domain.ports.input;

import com.lectoria_api.common.domain.model.verification.VerificationType;
import com.lectoria_api.users.domain.model.UserModel;

import java.util.UUID;

public interface VerificationUseCases {

    void createVerification(String usernameOrEmail, VerificationType verificationType, String email);
    void createVerification(UserModel user, VerificationType verificationType);
    void verifyCode(String usernameOrEmail, VerificationType verificationType, String code);
    boolean existsVerifyCodeByUserIdAndVerificationType(UUID userId, VerificationType verificationType);

}
