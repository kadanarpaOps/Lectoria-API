package com.lectoria_api.domain.ports.input;

import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import com.lectoria_api.verifications.domain.ports.input.SharedVerificationUseCases;

public interface VerificationUseCases extends SharedVerificationUseCases {

    void createVerification(String usernameOrEmail, VerificationType verificationType, String email);
    void verifyCode(String usernameOrEmail, VerificationType verificationType, String code);

}
