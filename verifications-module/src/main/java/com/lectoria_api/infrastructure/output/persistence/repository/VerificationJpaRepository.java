package com.lectoria_api.infrastructure.output.persistence.repository;

import com.lectoria_api.domain.models.enums.VerificationStatus;
import com.lectoria_api.common.domain.model.verification.VerificationType;
import com.lectoria_api.infrastructure.output.persistence.entities.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationJpaRepository extends JpaRepository<VerificationEntity, UUID> {

    VerificationEntity findByUser_UserIdAndVerificationType(UUID userUserId, VerificationType verificationType);
    void deleteByUser_UserId(UUID userUserId);
    boolean existsByUser_UserIdAndVerificationStatusAndVerificationType(UUID userUserId, VerificationStatus verificationStatus, VerificationType verificationType);

}
