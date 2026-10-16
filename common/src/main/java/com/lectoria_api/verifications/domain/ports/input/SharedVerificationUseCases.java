package com.lectoria_api.verifications.domain.ports.input;

import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;

import java.util.UUID;

public interface SharedVerificationUseCases {

    void createVerification(UserModel user, VerificationType verificationType);
    void existsVerifyCodeByUserIdAndVerificationType(UUID userId, VerificationType verificationType);

}
