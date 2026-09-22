package com.lectoria_api.common.infrastructure.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
public class ErrorResponseDTO {

    public Integer statusCode;
    public String message;
    public ErrorDetailsDTO details;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public LocalDateTime timestamp;

}
