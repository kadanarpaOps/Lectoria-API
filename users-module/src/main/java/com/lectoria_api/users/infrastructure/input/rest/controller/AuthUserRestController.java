package com.lectoria_api.users.infrastructure.input.rest.controller;

import com.lectoria_api.auth.domain.ports.input.AuthUseCases;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserEmailDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserPasswordDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserProfileDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserRoleDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.ResUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class AuthUserRestController {

    private final UserUseCases userService;
    private final AuthUseCases authService;
    private final UserRestMapper userMapper;

    @GetMapping("/profile")
    public ResponseEntity<ResUserDTO> getAuthUserProfile() {
        ResUserDTO resUserDTO = userMapper.toResDto(userService.findUserById(
                authService.getAuthUserId()
        ));
        return ResponseEntity.ok(resUserDTO);
    }

    @PatchMapping("/info")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody UpdateUserProfileDTO userDTO) {
        userService.updateUserInformation(
                userMapper.toModel(userDTO),
                authService.getAuthUserId()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/change-email")
    public ResponseEntity<Void> updateEmail(@Valid @RequestBody UpdateUserEmailDTO userDTO) {
        userService.updateUserEmail(
                userDTO.getNewEmail(),
                authService.getAuthUserId()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdateUserPasswordDTO userDTO) {
        userService.updateUserPassword(
                userDTO.getNewPassword(),
                authService.getAuthUserId()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/add-role")
    public ResponseEntity<Void> addUserRole(@Valid @RequestBody UpdateUserRoleDTO userDTO) {
        userService.addUserRole(
                userDTO.getRole(),
                authService.getAuthUserId()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/remove-role")
    public ResponseEntity<Void> removeUserRole(@Valid @RequestBody UpdateUserRoleDTO userDTO) {
        userService.removeUserRole(
                userDTO.getRole(),
                authService.getAuthUserId()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/toggle-status")
    public ResponseEntity<Void> toggleUserStatus() {
        userService.toggleUserStatus(authService.getAuthUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
