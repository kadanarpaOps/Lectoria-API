package com.lectoria_api.infrastructure.output.persistence.entities;

import com.lectoria_api.domain.models.enums.VerificationStatus;
import com.lectoria_api.common.domain.model.verification.VerificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_USER_VERIFICATIONS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID verificationId;
    private UUID userId;
    private String code;
    private Integer attempts;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private VerificationType verificationType;
    private VerificationStatus verificationStatus;
}