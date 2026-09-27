package com.lectoria_api.infrastructure.output.persistence.repositories;

import com.lectoria_api.domain.models.enums.VerificationStatus;
import com.lectoria_api.common.domain.model.verification.VerificationType;
import com.lectoria_api.infrastructure.output.persistence.entities.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationJpaRepository extends JpaRepository<VerificationEntity, UUID> {

    VerificationEntity findByUserIdAndVerificationType(UUID userId, VerificationType verificationType);
    boolean existsByUserIdAndVerificationStatusAndVerificationType(UUID userId, VerificationStatus verificationStatus, VerificationType verificationType);
    void deleteByUserId(UUID userId);

}
