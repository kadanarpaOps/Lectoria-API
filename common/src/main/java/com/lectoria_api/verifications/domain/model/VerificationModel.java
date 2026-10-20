package com.lectoria_api.verifications.domain.model;

import com.lectoria_api.verifications.domain.model.enums.VerificationStatus;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.lectoria_api.common.domain.constants.Constants.CODE_EXPIRATION_MINUTES;
import static com.lectoria_api.common.domain.constants.Constants.TIME_ZONE;
import static com.lectoria_api.common.domain.constants.Constants.VERIFICATION_EXPIRATION_MINUTES;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationModel {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UUID verificationId;
    private UserModel user;
    private String code;
    private Integer attempts;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private LocalDateTime codeCreationDate;
    private LocalDateTime codeExpirationDate;
    private VerificationType verificationType;
    private VerificationStatus verificationStatus;

    public void generateCode() {
        int number = RANDOM.nextInt(900000) + 100000;
        this.code = String.valueOf(number);
    }

    public void loadDates() {
        LocalDateTime now = LocalDateTime.now(TIME_ZONE);
        this.creationDate = now;
        this.expirationDate = now.plusMinutes(VERIFICATION_EXPIRATION_MINUTES);
        reloadCodeDates();
    }

    public void reloadCodeDates() {
        LocalDateTime now = LocalDateTime.now(TIME_ZONE);
        this.codeCreationDate = now;
        this.codeExpirationDate = now.plusMinutes(CODE_EXPIRATION_MINUTES);
    }

    public boolean isCodeExpired() {
        return LocalDateTime.now(TIME_ZONE)
                .isAfter(codeExpirationDate);
    }

    public boolean isVerificationExpired() {
        return LocalDateTime.now(TIME_ZONE)
                .isAfter(expirationDate);
    }

    public void renewCode() {
        generateCode();
        LocalDateTime now = LocalDateTime.now(TIME_ZONE);
        this.codeCreationDate = now;
        this.codeExpirationDate = now.plusMinutes(CODE_EXPIRATION_MINUTES);
    }

    public boolean isValidCode(String code) {
        return this.code.equals(code);
    }

}
