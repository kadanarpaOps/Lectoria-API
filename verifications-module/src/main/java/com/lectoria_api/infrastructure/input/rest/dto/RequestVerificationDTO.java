package com.lectoria_api.infrastructure.input.rest.dto;

import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RequestVerificationDTO {

    private String username;
    private String userEmail;
    private VerificationType verificationType;
    private String destinationEmail;

}
