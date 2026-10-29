package com.lectoria_api.application.service;

import com.lectoria_api.common.domain.constants.Constants;
import com.lectoria_api.domain.exceptions.business.ExistingVerificationRequestIsPendingException;
import com.lectoria_api.notifications.application.utils.NotificationBuilderUtil;
import com.lectoria_api.users.domain.exceptions.InvalidParametersForUserSearchException;
import com.lectoria_api.domain.exceptions.business.NoAttemptsAvailableException;
import com.lectoria_api.domain.exceptions.business.VerificationCodeIsExpiredException;
import com.lectoria_api.domain.exceptions.business.VerificationInvalidCodeException;
import com.lectoria_api.domain.exceptions.business.VerificationIsExpiredException;
import com.lectoria_api.domain.exceptions.business.VerificationNotFoundException;
import com.lectoria_api.domain.exceptions.business.WrongVerificationCreationRequestScopeException;
import com.lectoria_api.verifications.domain.model.VerificationModel;
import com.lectoria_api.verifications.domain.model.enums.VerificationStatus;
import com.lectoria_api.domain.ports.input.VerificationUseCases;
import com.lectoria_api.domain.ports.output.VerificationRepositoryPort;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.SharedAuthUseCases;
import com.lectoria_api.users.domain.ports.input.SharedUserUseCases;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.lectoria_api.domain.constants.Constants.ALLOWED_VERIFICATION_TYPES_AUTHENTICATED_CREATION;
import static com.lectoria_api.domain.constants.Constants.ALLOWED_VERIFICATION_TYPES_PUBLIC_CREATION;
import static com.lectoria_api.domain.constants.Constants.AUTH_SCOPE;
import static com.lectoria_api.domain.constants.Constants.PUBLIC_SCOPE;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class VerificationServicePort implements VerificationUseCases {

    private final VerificationRepositoryPort verificationRepository;
    private final SharedUserUseCases userService;
    private final SharedAuthUseCases authService;
    private final NotificationBuilderUtil notificationBuilderUtil;

    @Override
    public void createVerification(String username, String userEmail, VerificationType verificationType, String emailToBeNotified, UUID disabledUserId) {
        UUID authUserId = resolveAuthUserId();

        resolveVerificationCreationAvailability(authUserId, verificationType);

        UserModel existingUser = resolveExistingUser(authUserId,  username, userEmail, verificationType, disabledUserId);

        handleVerificationCreation(existingUser, verificationType, emailToBeNotified != null ? emailToBeNotified : existingUser.getUserEmail());
    }

    @Override
    public void createVerification(UserModel user, VerificationType verificationType) {
        handleVerificationCreation(user, verificationType, user.getUserEmail());
    }

    @Override
    public void verifyCode(String username, String email, VerificationType verificationType, String code, UUID disabledUserId) {
        VerificationModel existingVerification = resolveVerificationAvailability(username, email, verificationType, disabledUserId);

        if (existingVerification.isCodeExpired()) {
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
    public void regenerateCode(String username, String email, VerificationType verificationType, String emailToBeNotified, UUID disabledUserId) {
        VerificationModel existingVerification = resolveVerificationAvailability(username, email, verificationType, disabledUserId);

        UserModel existingUser = resolveExistingUser(resolveAuthUserId(), username, email, verificationType, disabledUserId);

        existingVerification.generateCode();
        existingVerification.setAttempts(5);
        existingVerification.renewCode();

        sendNotification(
                existingUser.getUsername(),
                existingVerification.getCode(),
                emailToBeNotified != null ? emailToBeNotified : existingUser.getUserEmail()
        );

        verificationRepository.save(existingVerification);
    }

    @Override
    public VerificationModel verifyVerificationExistence(UserModel user, VerificationType verificationType) {
        VerificationModel existingVerification = verificationRepository.findByUserIdAndVerificationType(
                user.getUserId(),
                verificationType
        );

        if (existingVerification == null)
            return null;

        if (existingVerification.isVerificationExpired()) {
            verificationRepository.deleteById(existingVerification.getVerificationId());
            return null;
        }

        return existingVerification;
    }

    @Override
    public VerificationModel verifyVerificationExistence(String username, String email, VerificationType verificationType, UUID disabledUserId) {
        UserModel existingUser = resolveExistingUser(resolveAuthUserId(), username, email, verificationType, disabledUserId);

        VerificationModel existingVerification = verificationRepository.findByUserIdAndVerificationType(
                existingUser.getUserId(),
                verificationType
        );

        if (existingVerification == null)
            return null;

        if (existingVerification.isVerificationExpired()) {
            verificationRepository.deleteById(existingVerification.getVerificationId());
            return null;
        }

        return existingVerification;
    }

    @Override
    @Transactional
    public void existsSuccessVerificationByUserIdAndVerificationType(UUID userId, VerificationType verificationType) {
        boolean exists = verificationRepository.existsByUserIdWithStatusVerifiedAndType(userId, verificationType);
        if (exists)  {
            verificationRepository.deleteByUserId(userId);
        } else {
            throw new VerificationNotFoundException();
        }
    }

    private VerificationModel resolveVerificationAvailability(String username, String email, VerificationType verificationType, UUID disabledUserId) {
        UserModel existingUser = resolveExistingUser(resolveAuthUserId(), username, email, verificationType, disabledUserId);

        VerificationModel existingVerification = verificationRepository.findByUserIdAndVerificationType(
                existingUser.getUserId(),
                verificationType
        );

        if (existingVerification == null ||
                !existingVerification.getVerificationStatus().name().equals(VerificationStatus.PENDING.name())) {
            throw new VerificationNotFoundException();
        }

        if (existingVerification.isVerificationExpired()) {
            verificationRepository.deleteById(existingVerification.getVerificationId());
            throw new VerificationIsExpiredException();
        }

        return existingVerification;
    }

    private UUID resolveAuthUserId() {
        try {
            return authService.getAuthUserId();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private void resolveVerificationCreationAvailability(UUID authUserId, VerificationType verificationType) {
        if (authUserId != null) {
            if (!ALLOWED_VERIFICATION_TYPES_AUTHENTICATED_CREATION.contains(verificationType))
                throw new WrongVerificationCreationRequestScopeException(PUBLIC_SCOPE);
            return;
        }
        if (!ALLOWED_VERIFICATION_TYPES_PUBLIC_CREATION.contains(verificationType))
            throw new WrongVerificationCreationRequestScopeException(AUTH_SCOPE);
    }

    private UserModel resolveExistingUser(UUID authUserId, String username, String email, VerificationType verificationType, UUID disabledUserId) {
        UserModel foundUser = new UserModel();
        boolean userEnabled = !VerificationType.VERIFY_REGISTRY.equals(verificationType) && !VerificationType.REACTIVATE_ACCOUNT.equals(verificationType);

        if (VerificationType.REACTIVATE_ACCOUNT.equals(verificationType))
            return userService.findUserById(disabledUserId);

        if (authUserId != null)
            foundUser = userService.findUserById(authUserId);
        else {
            boolean searchByUsername = username != null && !username.isEmpty();
            boolean searchByEmail = email != null && !email.isEmpty();
            if (searchByUsername && searchByEmail)
                throw new InvalidParametersForUserSearchException();
            if (searchByUsername)
                foundUser = userService.findAnyUserByUsername(username, userEnabled);
            if (searchByEmail)
                foundUser = userService.findAnyUserByUserEmail(email, userEnabled);
        }

        return foundUser;
    }

    private void handleVerificationCreation(UserModel user, VerificationType verificationType, String receiverEmail) {

        VerificationModel verificationModel = new VerificationModel();

        VerificationModel optionalExistingVerification = verificationRepository.findByUserIdAndVerificationType(
                user.getUserId(),
                verificationType
        );

        if (optionalExistingVerification != null) {
            if (optionalExistingVerification.isVerificationExpired()
                    || (optionalExistingVerification.getVerificationStatus() == VerificationStatus.VERIFIED
                    && canReplaceVerifiedVerification(verificationType))) {
                verificationRepository.deleteById(optionalExistingVerification.getVerificationId());
            } else {
                throw new ExistingVerificationRequestIsPendingException();
            }
        }

        verificationModel.generateCode();
        verificationModel.setAttempts(5);
        verificationModel.loadDates();
        verificationModel.setVerificationType(verificationType);
        verificationModel.setVerificationStatus(VerificationStatus.PENDING);
        verificationModel.setUser(user);

        sendNotification(
                user.getUsername(),
                verificationModel.getCode(),
                receiverEmail
        );

        verificationRepository.save(verificationModel);
    }

    private boolean canReplaceVerifiedVerification(VerificationType verificationType) {
        return verificationType == VerificationType.FORGOT_PASSWORD
                || verificationType == VerificationType.CHANGE_EMAIL
                || verificationType == VerificationType.CHANGE_PASSWORD
                || verificationType == VerificationType.DISABLE_ACCOUNT;
    }

    private void sendNotification(String username, String otp, String receiverEmail) {
        notificationBuilderUtil.sendOtpNotification(
                username,
                otp,
                String.valueOf(Constants.CODE_EXPIRATION_MINUTES),
                receiverEmail
        );
    }

}
