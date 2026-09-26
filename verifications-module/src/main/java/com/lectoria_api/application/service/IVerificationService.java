package com.lectoria_api.application.service;

import com.lectoria_api.common.domain.model.verification.VerificationType;

import java.util.UUID;

public interface IVerificationService {
    void saveVerificationRecord(String usernameOrEmail, VerificationType verificationType, String newEmail);
    void verifyCode(String usernameOrEmail, VerificationType verificationType, String code);
    boolean existsVerifyCodeByUserIdAndVerificationType(UUID userId, VerificationType verificationType);
}