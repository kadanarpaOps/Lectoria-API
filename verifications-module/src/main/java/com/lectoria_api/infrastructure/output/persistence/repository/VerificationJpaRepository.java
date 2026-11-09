package com.lectoria_api.infrastructure.output.persistence.repository;

import com.lectoria_api.infrastructure.output.persistence.entities.VerificationEntity;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationJpaRepository extends JpaRepository<VerificationEntity, UUID> {

    VerificationEntity findByUser_UserIdAndVerificationType(UUID userUserId, VerificationType verificationType);
    void deleteByUser_UserIdAndVerificationType(UUID userUserId, VerificationType verificationType);

}
