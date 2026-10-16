package com.lectoria_api.infrastructure.input.rest.dto;

import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ValidateVerificationCodeDTO {

    private String usernameOrEmail;
    private String code;
    private VerificationType verificationType;

}
