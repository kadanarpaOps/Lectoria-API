package com.lectoria_api.common.infrastructure.input.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ErrorDetailsDTO {

    private String errorName;

}
