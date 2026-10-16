package com.lectoria_api.infrastructure.output.persistence.entities;

import com.lectoria_api.domain.models.enums.VerificationStatus;
import com.lectoria_api.common.domain.model.verification.VerificationType;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @Column(name = "VERIFICATION_ID")
    private UUID verificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = ""), nullable = false)
    private UserEntity user;

    @Column(name = "VERIFICATION_CODE")
    private String code;
    @Column(name = "VERIFICATION_ATTEMPTS")
    private Integer attempts;
    @Column(name = "VERIFICATION_CREATION_DATE")
    private LocalDateTime creationDate;
    @Column(name = "VERIFICATION_EXPIRATION_DATE")
    private LocalDateTime expirationDate;
    @Column(name = "VERIFICATION_TYPE")
    private VerificationType verificationType;
    @Column(name = "VERIFICATION_STATUS")
    private VerificationStatus verificationStatus;

}
