package com.lectoria_api.infrastructure.input.rest.controller;

import com.lectoria_api.domain.ports.input.VerificationUseCases;
import com.lectoria_api.infrastructure.input.rest.dto.CreateVerificationDTO;
import com.lectoria_api.infrastructure.input.rest.dto.ValidateVerificationCodeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verifications")
@RequiredArgsConstructor
public class VerificationRestController {

    private final VerificationUseCases verificationService;

    @PostMapping
    public ResponseEntity<Void> createVerificationRequest(@RequestBody CreateVerificationDTO createVerificationDTO) {
        verificationService.createVerification(
                createVerificationDTO.getUsernameOrEmail(),
                createVerificationDTO.getVerificationType(),
                createVerificationDTO.getDestinationEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyCode(@RequestBody ValidateVerificationCodeDTO verificationCodeDTO) {
        verificationService.verifyCode(
                verificationCodeDTO.getUsernameOrEmail(),
                verificationCodeDTO.getVerificationType(),
                verificationCodeDTO.getCode()
        );
        return ResponseEntity.ok().build();
    }

}
