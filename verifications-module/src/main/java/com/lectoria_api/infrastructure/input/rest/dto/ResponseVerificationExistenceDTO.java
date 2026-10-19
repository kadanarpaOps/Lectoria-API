package com.lectoria_api.infrastructure.input.rest.dto;

import com.lectoria_api.verifications.domain.model.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter @Setter
public class ResponseVerificationExistenceDTO {

    boolean exists;
    VerificationStatus verificationStatus;

}
