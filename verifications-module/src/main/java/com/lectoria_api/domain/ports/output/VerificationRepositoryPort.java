package com.lectoria_api.domain.ports.output;

import com.lectoria_api.verifications.domain.model.VerificationModel;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;

import java.util.UUID;

public interface VerificationRepositoryPort {

    VerificationModel findByUserIdAndVerificationType(UUID userId, VerificationType verificationType);
    void save(VerificationModel verificationModel);
    void deleteById(UUID verificationId);
    void deleteByUserId(UUID userId);
    boolean existsByUserIdWithStatusVerifiedAndType(UUID userId, VerificationType verificationType);

}
