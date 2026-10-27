package com.lectoria_api.users.domain.ports.input;

import java.util.UUID;

public interface AdminUseCases {

    // Just Admin Features
    void addUserRole(String newRole, UUID userId);
    void removeUserRole(String removeRole, UUID userId);
    void toggleUserStatus(UUID userId);

}
