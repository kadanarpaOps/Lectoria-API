package com.lectoria_api.users.domain.ports.input;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;

import java.util.UUID;

public interface UserUseCases extends SharedUserUseCases {

    // Registry
    void registerUser(UserModel user);
    void verifyRegistry(String username, String email);

    // Find
    PageResponse<UserModel> pageUsers(PaginationRequest paginationRequest, UserFilters userFilters);

    // Update
    void updateUserInformation(UserModel toUpdateInfo, UUID userId);
    void updateUserEmail(String email, UUID userId);
    void updateUserPassword(String password, UUID userId);

    // Just Admin Features
    void addUserRole(String newRole, UUID userId);
    void removeUserRole(String removeRole, UUID userId);
    void toggleUserStatus(UUID userId);

    // Disable Account
    void disableUserAccount(UUID userId);

    // Reactivate Account
    void reactivateUserAccount(String username, String email, String newPassword);

}
