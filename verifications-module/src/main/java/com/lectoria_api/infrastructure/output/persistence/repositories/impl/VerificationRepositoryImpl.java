package com.lectoria_api.infrastructure.output.persistence.repositories.impl;

import com.lectoria_api.domain.models.VerificationModel;
import com.lectoria_api.domain.models.enums.VerificationStatus;
import com.lectoria_api.common.domain.model.verification.VerificationType;
import com.lectoria_api.domain.ports.output.repository.VerificationRepository;
import com.lectoria_api.infrastructure.output.persistence.mapper.VerificationInfraMapper;
import com.lectoria_api.infrastructure.output.persistence.repositories.VerificationJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VerificationRepositoryImpl implements VerificationRepository {
    private final VerificationJpaRepository jpaRepository;
    private final VerificationInfraMapper mapper;

    @Override
    public VerificationModel findByUserIdAndVerificationType(UUID userId, VerificationType verificationType) {
        return mapper.toDomain(jpaRepository
                .findByUserIdAndVerificationType(userId, verificationType));
    }

    @Override
    public void save(VerificationModel verificationModel) {
        jpaRepository.save(mapper.toInfra(verificationModel));
    }

    @Override
    public void deleteById(UUID verificationId) {
        jpaRepository.deleteById(verificationId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId((userId));
    }

    @Override
    @Transactional
    public boolean existsByUserIdWithStatusVerifiedAndType(UUID userId, VerificationType verificationType) {
        return jpaRepository.existsByUserIdAndVerificationStatusAndVerificationType(
                userId,
                VerificationStatus.VERIFIED,
                verificationType
        );
    }
}
