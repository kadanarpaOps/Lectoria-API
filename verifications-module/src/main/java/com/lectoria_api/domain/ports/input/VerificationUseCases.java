package com.lectoria_api.domain.ports.input;

import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import com.lectoria_api.verifications.domain.ports.input.SharedVerificationUseCases;

public interface VerificationUseCases extends SharedVerificationUseCases {

    void createVerification(String username, String userEmail, VerificationType verificationType, String emailTobeNotified);
    void verifyCode(String username, String email, VerificationType verificationType, String code);
    void regenerateCode(String username, String email, VerificationType verificationType, String emailToBeNotified);

}
