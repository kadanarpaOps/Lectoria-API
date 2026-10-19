package com.lectoria_api.infrastructure.input.rest.dto;

import com.lectoria_api.common.domain.model.verification.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CreateVerificationDTO {

    private String usernameOrEmail;
    private VerificationType verificationType;
    private String destinationEmail;

}
