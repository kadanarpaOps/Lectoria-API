package com.lectoria_api.users.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.RegisterUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.ResUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<PageResponse<ResUserDTO>> pageUsers(
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
        List<ResUserDTO> userList = modelPage.getData().stream().map(userMapper::toResDto).toList();
        PageResponse<ResUserDTO> pageResponse = PageResponse.<ResUserDTO>builder()
                .data(userList)
                .metaData(modelPage.getMetaData())
                .build();

        return ResponseEntity.ok(pageResponse);
    }

}
