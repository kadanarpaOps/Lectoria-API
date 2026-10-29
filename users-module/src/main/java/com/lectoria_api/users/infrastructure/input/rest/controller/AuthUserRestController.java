package com.lectoria_api.users.infrastructure.input.rest.controller;

import com.lectoria_api.auth.domain.ports.input.AuthUseCases;
import com.lectoria_api.common.infrastructure.cloudinary.CloudinaryFileUpdateUtil;
import com.lectoria_api.common.infrastructure.cloudinary.CloudinaryFileUpdateUtil.ImageReference;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserEmailDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserPasswordDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserProfileDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserRoleDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.ResUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class AuthUserRestController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthUserRestController.class);

    private final UserUseCases userService;
    private final AuthUseCases authService;
    private final UserRestMapper userMapper;
    private final CloudinaryFileUpdateUtil cloudinary;

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

    @PatchMapping(value = "/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> updateProfileImage(@RequestPart("image") MultipartFile image) {
        var userId = authService.getAuthUserId();
        var user = userService.findUserById(userId);
        String oldUrl = user.getUserDetails() == null ? null : user.getUserDetails().getProfileImageUrl();
        ImageReference uploaded = cloudinary.uploadImage(image, "lectoria/profiles");
        try {
            userService.updateUserProfileImage(uploaded.url(), userId);
        } catch (RuntimeException exception) {
            deleteImageBestEffort(uploaded.publicId());
            throw exception;
        }
        deleteImageBestEffort(cloudinary.publicIdFromUrl(oldUrl));
        return ResponseEntity.ok(Map.of("profileImageUrl", uploaded.url()));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/profile-image")
    public ResponseEntity<Void> removeProfileImage() {
        var userId = authService.getAuthUserId();
        var user = userService.findUserById(userId);
        String oldUrl = user.getUserDetails() == null ? null : user.getUserDetails().getProfileImageUrl();
        userService.updateUserProfileImage(null, userId);
        deleteImageBestEffort(cloudinary.publicIdFromUrl(oldUrl));
        return ResponseEntity.noContent().build();
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

    @PatchMapping("/disable-account")
    public ResponseEntity<Void> disableUserAccount() {
        userService.disableUserAccount(authService.getAuthUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private void deleteImageBestEffort(String publicId) {
        try {
            cloudinary.deleteImage(publicId);
        } catch (RuntimeException ignored) {
            LOG.warn("Profile image cleanup failed for publicId '{}'; persisted profile-image change remains in effect",
                    publicId, ignored);
        }
    }

}
