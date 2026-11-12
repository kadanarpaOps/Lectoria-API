package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.stories.application.SynopsisGenerationService;
import com.lectoria_api.stories.infrastructure.input.rest.dto.SynopsisGenerationRequest;
import com.lectoria_api.stories.infrastructure.input.rest.dto.SynopsisGenerationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/story/ai")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "lectoria.ai", name = "enabled", havingValue = "true")
public class SynopsisGenerationController {
    private final SynopsisGenerationService synopsisGeneration;

    @PostMapping("/synopsis")
    public SynopsisGenerationResponse generateSynopsis(@Valid @RequestBody SynopsisGenerationRequest request) {
        return synopsisGeneration.generate(request);
    }
}
