package com.lectoria_api.domain.models;

import com.lectoria_api.domain.models.enums.VerificationStatus;
import com.lectoria_api.common.domain.model.verification.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationModel {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UUID verificationId;
    private UUID userId;
    private String code;
    private Integer attempts;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private VerificationType verificationType;
    private VerificationStatus verificationStatus;

    public void generateCode() {
        int number = RANDOM.nextInt(900000) + 100000;
        this.code = String.valueOf(number);
    }

    public void loadDates() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Bogota"));
        this.creationDate = now;
        this.expirationDate = now.plusMinutes(5);
    }

    public boolean isExpired() {
        return LocalDateTime.now(ZoneId.of("America/Bogota"))
                .isAfter(expirationDate);
    }

    public boolean isValidCode(String code) {
        return this.code.equals(code);
    }
}