package com.lectoria_api.auth.infrastructure.input.rest.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ValidationResponseDTO {

    private boolean active;

}
