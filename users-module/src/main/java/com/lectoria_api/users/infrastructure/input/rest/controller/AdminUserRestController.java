package com.lectoria_api.users.infrastructure.input.rest.controller;

import com.lectoria_api.users.domain.ports.input.AdminUseCases;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserRoleDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminUserRestController {

    private final AdminUseCases adminService;

    @PatchMapping("/add-role")
    public ResponseEntity<Void> addUserRole(@Valid @RequestBody UpdateUserRoleDTO userDTO) {
        adminService.addUserRole(
                userDTO.getRole(),
                userDTO.getUserId()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/remove-role")
    public ResponseEntity<Void> removeUserRole(@Valid @RequestBody UpdateUserRoleDTO userDTO) {
        adminService.removeUserRole(
                userDTO.getRole(),
                userDTO.getUserId()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/toggle-user-status")
    public ResponseEntity<Void> toggleUserStatus(@RequestParam UUID userId) {
        adminService.toggleUserStatus(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
