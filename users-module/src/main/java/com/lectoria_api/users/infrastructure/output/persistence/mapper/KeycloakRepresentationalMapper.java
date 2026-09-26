package com.lectoria_api.users.infrastructure.output.persistence.mapper;

import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.model.UserModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeycloakRepresentationalMapper {

    public UserRepresentation toRepresentation(UserModel userModel) {
        if (userModel == null) return null;
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userModel.getUsername());
        userRepresentation.setEmail(userModel.getUserEmail());
        userRepresentation.setEnabled(userModel.isUserEnabled());
        if (userModel.getUserId() != null) {
            userRepresentation.setId(userModel.getUserId().toString());
        }
        userRepresentation.setRealmRoles(userModel.getUserRoles().stream().map(
                RoleModel::getRoleName
        ).toList());
        return userRepresentation;
    }

    public UserModel toModel(UserRepresentation userRepresentation) {
        if (userRepresentation == null) return null;
        return UserModel.builder()
                .userId(UUID.fromString(userRepresentation.getId()))
                .userEmail(userRepresentation.getEmail())
                .build();
    }

}
