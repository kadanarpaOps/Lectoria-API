package com.lectoria_api.users.domain.ports.input;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;

import java.util.UUID;

public interface AdminUseCases {

    // Just Admin Features
    void addUserRole(String newRole, UUID userId);
    void removeUserRole(String removeRole, UUID userId);
    void toggleUserStatus(UUID userId);
    PageResponse<UserModel> pageUsers(PaginationRequest pagination, UserFilters filters);

}
