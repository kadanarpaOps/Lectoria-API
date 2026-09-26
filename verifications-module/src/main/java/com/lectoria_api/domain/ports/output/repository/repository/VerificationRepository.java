package com.lectoria_api.domain.ports.output.repository;

import com.lectoria_api.domain.models.VerificationModel;
import com.lectoria_api.common.domain.model.verification.VerificationType;

import java.util.UUID;

public interface VerificationRepository {
    VerificationModel findByUserIdAndVerificationType(UUID userId, VerificationType verificationType);
    void save(VerificationModel verificationModel);
    void deleteById(UUID verificationId);
    void deleteByUserId(UUID userId);
    boolean existsByUserIdWithStatusVerifiedAndType(UUID userId, VerificationType verificationType);
}
