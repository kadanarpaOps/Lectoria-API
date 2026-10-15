package com.lectoria_api.application.service;

import com.lectoria_api.common.domain.model.verification.VerificationType;
import com.lectoria_api.domain.ports.input.VerificationUseCases;
import com.lectoria_api.domain.ports.output.VerificationRepositoryPort;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServicePort implements VerificationUseCases {

    private final VerificationRepositoryPort verificationRepository;
    private final UserUseCases userService;

    @Override
    public void createVerification(String usernameOrEmail, VerificationType verificationType, String email) {

    }

    @Override
    public void createVerification(UserModel user, VerificationType verificationType) {

    }

    @Override
    public void verifyCode(String usernameOrEmail, VerificationType verificationType, String code) {

    }

    @Override
    public boolean existsVerifyCodeByUserIdAndVerificationType(UUID userId, VerificationType verificationType) {
        return false;
    }
}
