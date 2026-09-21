package com.lectoria_api.users.domain.ports.input;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;

import java.util.UUID;

public interface UserUseCases {

    PageResponse<UserModel> pageUsers(PaginationRequest paginationRequest, UserFilters userFilters);
    UserModel findUserById(UUID userId);
    UserModel findUserByEmail(String userEmail);
    void registerUser(UserModel user);
    void updateUserInformation(UserModel toUpdateInfo, UUID userId);
    void updateUserEmail(String email, UUID userId);
    void updateUserPassword(String password, UUID userId);
    void addUserRole(String newRole, UUID userId);
    void removeUserRole(String newRole, UUID userId);
    void toggleUserStatus(UUID userId);

}
