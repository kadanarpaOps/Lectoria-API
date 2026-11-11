package com.lectoria_api.users.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.RegisterUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.ResetPasswordDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.PublicUserDirectoryEntryDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.PublicUserProfileDTO;
import com.lectoria_api.users.infrastructure.input.rest.mapper.UserRestMapper;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.lectoria_api.users.domain.constants.Constants.ID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserUseCases userService;
    private final UserRestMapper userMapper;

    @PostMapping
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterUserDTO userDTO) {
        userService.registerUser(userMapper.toModel(userDTO));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify-registry")
    public ResponseEntity<Void> verifyRegistry(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        userService.verifyRegistry(username, email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
        userService.resetUserPassword(request.getUsername(), request.getUserEmail(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<PublicUserDirectoryEntryDTO>> pageUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "0") int pageNumber
    ) {
        PaginationRequest pageRequest = PaginationRequest.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber).build();
        UserFilters userFilters = UserFilters.builder()
                .username(username)
                .userEmail(userEmail)
                .build();

        PageResponse<UserModel> modelPage = userService.pageUsers(pageRequest, userFilters);
        List<PublicUserDirectoryEntryDTO> userList = modelPage.getData().stream().map(userMapper::toPublicDirectoryEntry).toList();
        PageResponse<PublicUserDirectoryEntryDTO> pageResponse = PageResponse.<PublicUserDirectoryEntryDTO>builder()
                .data(userList)
                .metaData(modelPage.getMetaData())
                .build();

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{userId}/public-profile")
    public ResponseEntity<PublicUserProfileDTO> publicProfile(@PathVariable UUID userId) {
        var user = userService.findUserById(userId);
        if (!user.isUserEnabled() || user.getUserDetails() == null
                || !user.getUserDetails().isRegisteredVerified()) {
            throw new UserWithFieldNotExistsException(ID);
        }
        return ResponseEntity.ok(PublicUserProfileDTO.builder()
                .userId(user.getUserId().toString())
                .username(user.getUsername())
                .profileImageUrl(user.getUserDetails().getProfileImageUrl())
                .memberSince(user.getUserDetails().getCreatedAt())
                .build());
    }

}
