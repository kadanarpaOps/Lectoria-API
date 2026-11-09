package com.lectoria_api.users.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.users.domain.ports.input.AdminUseCases;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.AdministrativeResUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.mapper.UserRestMapper;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserRoleDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminUserRestController {

    private final AdminUseCases adminService;
    private final UserRestMapper userMapper;

    @GetMapping("/users")
    public ResponseEntity<PageResponse<AdministrativeResUserDTO>> pageUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) {
        PaginationRequest pagination = PaginationRequest.builder()
                .pageSize(pageSize).pageNumber(pageNumber).build();
        UserFilters filters = UserFilters.builder()
                .username(username)
                .userEmail(userEmail)
                .build();
        PageResponse<UserModel> users = adminService.pageUsers(pagination, filters);
        List<AdministrativeResUserDTO> data = users.getData().stream()
                .map(userMapper::toAdministrativeResDto).toList();
        return ResponseEntity.ok(PageResponse.<AdministrativeResUserDTO>builder()
                .data(data).metaData(users.getMetaData()).build());
    }

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
