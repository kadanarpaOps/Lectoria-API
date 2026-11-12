package com.lectoria_api.stories.infrastructure.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SynopsisGenerationRequest(
        @NotBlank
        @Size(max = 2000)
        String idea,
        @Size(max = 10)
        List<@NotBlank @Size(max = 40) String> tags
) {
}
