package com.lectoria_api.users.domain.ports.output;

import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import com.lectoria_api.users.domain.exceptions.keycloak.KeycloakRoleNotFoundException;
import com.lectoria_api.users.domain.model.UserModel;

public interface KeycloakConnectorPort {

    String registerKeycloakUser(UserModel user) throws FailedOperationException, UserWithFieldNotExistsException, KeycloakRoleNotFoundException;
    void updateKeycloakUser(String userId, UserModel user) throws FailedOperationException;
    void updateKeycloakUserCredentials(String userId, String newPassword);
    UserModel findKeycloakUserByEmail(String email) throws FailedOperationException, UserWithFieldNotExistsException;
    void toggleKeycloakUserStatus(String userId) throws FailedOperationException;
    void rollbackKeycloakUserCreation(String userId) throws FailedOperationException;

}
