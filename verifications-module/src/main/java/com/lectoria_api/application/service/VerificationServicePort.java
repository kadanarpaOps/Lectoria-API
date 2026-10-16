package com.lectoria_api.application.service;

import com.lectoria_api.domain.exceptions.business.NoAttemptsAvailableException;
import com.lectoria_api.domain.exceptions.business.VerificationCodeIsExpiredException;
import com.lectoria_api.domain.exceptions.business.VerificationInvalidCodeException;
import com.lectoria_api.domain.exceptions.business.VerificationNotFoundException;
import com.lectoria_api.domain.models.VerificationModel;
import com.lectoria_api.domain.models.enums.VerificationStatus;
import com.lectoria_api.domain.ports.input.VerificationUseCases;
import com.lectoria_api.domain.ports.output.VerificationRepositoryPort;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.SharedUserUseCases;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class VerificationServicePort implements VerificationUseCases {

    private final VerificationRepositoryPort verificationRepository;
    private final SharedUserUseCases userService;

    @Override
    public void createVerification(String usernameOrEmail, VerificationType verificationType, String email) {
        UserModel existingUser = userService.findUserByUsernameOrEmail(usernameOrEmail);
        handleVerificationCreation(existingUser, verificationType);
    }

    @Override
    public void createVerification(UserModel user, VerificationType verificationType) {
        handleVerificationCreation(user, verificationType);
    }

    @Override
    public void verifyCode(String usernameOrEmail, VerificationType verificationType, String code) {
        UserModel existingUser = userService.findUserByUsernameOrEmail(usernameOrEmail);

        VerificationModel existingVerification = verificationRepository.findByUserIdAndVerificationType(
                existingUser.getUserId(),
                verificationType
        );

        if (existingVerification == null ||
                !existingVerification.getVerificationStatus().name().equals(VerificationStatus.WAITING.name())) {
            throw new VerificationNotFoundException();
        }

        if (existingVerification.isExpired()) {
            verificationRepository.deleteById(existingVerification.getVerificationId());
            throw new VerificationCodeIsExpiredException();
        }

        if (existingVerification.getAttempts() == 0) {
            verificationRepository.deleteById(existingVerification.getVerificationId());
            throw new NoAttemptsAvailableException();
        }

        if (!existingVerification.isValidCode(code)) {
            Integer attempts = existingVerification.getAttempts() - 1;
            existingVerification.setAttempts(attempts);
            verificationRepository.save(existingVerification);
            throw new VerificationInvalidCodeException();
        } else {
            existingVerification.setVerificationStatus(VerificationStatus.VERIFIED);
            verificationRepository.save(existingVerification);
        }
    }

    @Override
    @Transactional
    public void existsVerifyCodeByUserIdAndVerificationType(UUID userId, VerificationType verificationType) {
        boolean exists = verificationRepository.existsByUserIdWithStatusVerifiedAndType(userId, verificationType);
        if (exists)  {
            verificationRepository.deleteByUserId(userId);
        } else {
            throw new VerificationNotFoundException();
        }
    }

    private void handleVerificationCreation(UserModel user, VerificationType verificationType) {

        VerificationModel verificationModel = new VerificationModel();

        VerificationModel optionalExistingVerification = verificationRepository.findByUserIdAndVerificationType(
                user.getUserId(),
                verificationType
        );

        if (optionalExistingVerification != null) {
            verificationRepository.deleteById(optionalExistingVerification.getVerificationId());
        }

        verificationModel.generateCode();
        verificationModel.setAttempts(5);
        verificationModel.loadDates();
        verificationModel.setVerificationType(verificationType);
        verificationModel.setVerificationStatus(VerificationStatus.WAITING);
        verificationModel.setUser(user);
        verificationRepository.save(verificationModel);
    }

}
