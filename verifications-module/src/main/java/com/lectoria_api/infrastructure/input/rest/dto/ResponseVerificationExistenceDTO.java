package com.lectoria_api.infrastructure.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lectoria_api.verifications.domain.model.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.lectoria_api.common.domain.constants.Constants.DATE_FORMAT;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter @Setter
public class ResponseVerificationExistenceDTO {

    boolean exists;
    VerificationStatus verificationStatus;
    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime expirationDate;


}
