package com.lectoria_api.infrastructure.input.rest.controller;

import com.lectoria_api.verifications.domain.model.VerificationModel;
import com.lectoria_api.domain.ports.input.VerificationUseCases;
import com.lectoria_api.infrastructure.input.rest.dto.RequestVerificationDTO;
import com.lectoria_api.infrastructure.input.rest.dto.ResponseVerificationExistenceDTO;
import com.lectoria_api.infrastructure.input.rest.dto.ValidateVerificationCodeDTO;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verifications")
@RequiredArgsConstructor
public class VerificationRestController {

    private final VerificationUseCases verificationService;

    @PostMapping
    public ResponseEntity<Void> createVerificationRequest(@RequestBody RequestVerificationDTO requestVerificationDTO) {
        verificationService.createVerification(
                requestVerificationDTO.getUsername(),
                requestVerificationDTO.getUserEmail(),
                requestVerificationDTO.getVerificationType(),
                requestVerificationDTO.getDestinationEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyCode(@RequestBody ValidateVerificationCodeDTO verificationCodeDTO) {
        verificationService.verifyCode(
                verificationCodeDTO.getUsername(),
                verificationCodeDTO.getUserEmail(),
                verificationCodeDTO.getVerificationType(),
                verificationCodeDTO.getCode()
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/regenerate-code")
    public ResponseEntity<Void> regenerateCode(@RequestBody RequestVerificationDTO regenerateCodeDTO) {
        verificationService.regenerateCode(
                regenerateCodeDTO.getUsername(),
                regenerateCodeDTO.getUserEmail(),
                regenerateCodeDTO.getVerificationType(),
                regenerateCodeDTO.getDestinationEmail()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/verify/verification-existence")
    public ResponseEntity<ResponseVerificationExistenceDTO> verifyVerificationExistence(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam VerificationType verificationType
    ) {
        VerificationModel existingVerification = verificationService.verifyVerificationExistence(
                username, email, verificationType
        );
        ResponseVerificationExistenceDTO response = ResponseVerificationExistenceDTO.builder()
                .exists(
                        existingVerification != null)
                .verificationStatus(
                        existingVerification != null ? existingVerification.getVerificationStatus() : null)
                .expirationDate(
                        existingVerification != null ? existingVerification.getExpirationDate() : null)
                .build();

        return ResponseEntity.ok(response);
    }

}
