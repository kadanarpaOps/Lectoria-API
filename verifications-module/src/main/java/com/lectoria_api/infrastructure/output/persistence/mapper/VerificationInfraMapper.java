package com.lectoria_api.infrastructure.output.persistence.mapper;

import com.lectoria_api.domain.models.VerificationModel;
import com.lectoria_api.infrastructure.output.persistence.entities.VerificationEntity;
import org.springframework.stereotype.Component;

@Component
public interface VerificationInfraMapper {
    VerificationModel toDomain(VerificationEntity verificationEntity);
    VerificationEntity toInfra(VerificationModel verificationModel);
}
