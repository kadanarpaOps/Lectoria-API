package com.lectoria_api.users.infrastructure.output.adapter;

import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import com.lectoria_api.users.domain.exceptions.keycloak.KeycloakRoleNotFoundException;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.output.KeycloakConnectorPort;
import com.lectoria_api.users.infrastructure.output.persistence.mapper.KeycloakRepresentationalMapper;
import com.lectoria_api.users.infrastructure.output.persistence.repository.KeycloakUserRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.lectoria_api.users.domain.constants.Constants.CREATION_OP;
import static com.lectoria_api.users.domain.constants.Constants.DELETION_OP;
import static com.lectoria_api.users.domain.constants.Constants.FIELD_WITH_VALUE_NOT_EXIST;
import static com.lectoria_api.users.domain.constants.Constants.ID;
import static com.lectoria_api.users.domain.constants.Constants.KEYCLOAK_ERR_CONNECTION;
import static com.lectoria_api.users.domain.constants.Constants.SEARCH_OP;
import static com.lectoria_api.users.domain.constants.Constants.UPDATE_OP;

@Component
@RequiredArgsConstructor
public class KeycloakConnectorAdapter implements KeycloakConnectorPort {

    private final KeycloakUserRepository keycloakRepository;
    private final KeycloakRepresentationalMapper keycloakMapper;

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakConnectorAdapter.class);

    /**
     * @param user Represents the Model to be created in Keycloak Realm
     * @return The id of creation, a UUID
     * @throws FailedOperationException if the connection is unstable or there is any issue in the Operation
     * @throws KeycloakRoleNotFoundException if the Role does not exist in Keycloak Realm
     */
    @Override
    public String registerKeycloakUser(UserModel user) throws FailedOperationException, KeycloakRoleNotFoundException {
        String userId;
        UserRepresentation keycloakUser = keycloakMapper.toRepresentation(user);

        assignCredentials(keycloakUser, user.getUserPassword());

        keycloakUser.getRealmRoles().forEach(roleToAdd -> {
            try {
                keycloakRepository.getRoleByName(roleToAdd);
            } catch (NotFoundException e) {
                throw new KeycloakRoleNotFoundException(roleToAdd);
            } catch  (RuntimeException e) {
                throw new FailedOperationException(CREATION_OP, KEYCLOAK_ERR_CONNECTION);
            }
        });

        LOG.info("[KEYCLOAK_CONNECTOR] User to be stored on Keycloak: {}", keycloakUser);

        try {
            userId = keycloakRepository.save(keycloakUser);
        } catch (RuntimeException e) {
            LOG.error("[KEYCLOAK_CONNECTOR] Error Creating User: {}", e.getMessage());
            throw new FailedOperationException(CREATION_OP, KEYCLOAK_ERR_CONNECTION);
        }

        return userId;
    }

    @Override
    public void updateKeycloakUser(String userId, UserModel user) throws FailedOperationException {
        try {
            keycloakRepository.getById(userId);
        } catch (RuntimeException e) {
            throw new FailedOperationException(SEARCH_OP, String.format(FIELD_WITH_VALUE_NOT_EXIST, ID, userId));
        }
        try {
            keycloakRepository.update(userId, keycloakMapper.toRepresentation(user));
        } catch (RuntimeException e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    @Override
    public void updateKeycloakUserCredentials(String userId, String newPassword) {
        UserRepresentation userFromKeycloak = this.findUserFromKeycloakById(userId);
        assignCredentials(userFromKeycloak, newPassword);
        try {
            keycloakRepository.update(userId, userFromKeycloak);
        } catch (RuntimeException e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    @Override
    public UserModel findKeycloakUserByEmail(String email) throws FailedOperationException, UserWithFieldNotExistsException {
        UserRepresentation userFromKeycloak = keycloakRepository.getByEmail(email);
        if (userFromKeycloak == null) {
            return null;
        }
        return keycloakMapper.toModel(userFromKeycloak);
    }

    @Override
    public void toggleKeycloakUserStatus(String userId) throws FailedOperationException {
        try {
            UserRepresentation userToKeycloak = keycloakRepository.getById(userId);
            userToKeycloak.setEnabled(!userToKeycloak.isEnabled());
            keycloakRepository.update(userId, userToKeycloak);
        } catch(RuntimeException e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    @Override
    public void rollbackKeycloakUserCreation(String userId) throws FailedOperationException {
        try {
            keycloakRepository.delete(userId);
        } catch (RuntimeException e) {
            throw new FailedOperationException(DELETION_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    private UserRepresentation findUserFromKeycloakById(String userId) {
        UserRepresentation userFromKeycloak = keycloakRepository.getById(userId);
        if (userFromKeycloak == null) {
            throw new FailedOperationException(SEARCH_OP, String.format(FIELD_WITH_VALUE_NOT_EXIST, ID, userId));
        }
        return userFromKeycloak;
    }

    private void assignCredentials(UserRepresentation user, String password) {
        user.setCredentials(Collections.emptyList());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));
    }

}
