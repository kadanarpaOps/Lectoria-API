package com.lectoria_api.users.application.service;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import com.lectoria_api.users.domain.exceptions.business.SameFieldValueOnUpdateException;
import com.lectoria_api.users.domain.exceptions.business.UserAlreadyHaveThatRoleException;
import com.lectoria_api.users.domain.exceptions.business.UserNotHaveThatRoleException;
import com.lectoria_api.users.domain.exceptions.business.UserRolesListCantBeEmptyException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldAlreadyExistsException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.RoleUseCases;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import com.lectoria_api.users.domain.ports.output.KeycloakConnectorPort;
import com.lectoria_api.users.domain.ports.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.lectoria_api.users.domain.constants.Constants.ADMINISTRATOR;
import static com.lectoria_api.users.domain.constants.Constants.AT_LEAST_ONE_FIELD_REQUIRED;
import static com.lectoria_api.users.domain.constants.Constants.CREATION_OP;
import static com.lectoria_api.users.domain.constants.Constants.DATABASE_ERR_CONNECTION;
import static com.lectoria_api.users.domain.constants.Constants.EMAIL;
import static com.lectoria_api.users.domain.constants.Constants.ID;
import static com.lectoria_api.users.domain.constants.Constants.KEYCLOAK_ERR_CONNECTION;
import static com.lectoria_api.users.domain.constants.Constants.UPDATE_OP;
import static com.lectoria_api.users.domain.constants.Constants.USERNAME;
import static com.lectoria_api.users.domain.constants.Constants.USER_WITH_ROLE_INVALID;

@Service
@RequiredArgsConstructor
public class UserServicePort implements UserUseCases {

    private final UserRepositoryPort userRepository;
    private final KeycloakConnectorPort keycloakConnector;
    private final RoleUseCases roleUseCases;

    private static final Logger LOG = LoggerFactory.getLogger(UserServicePort.class);

    /**
     * This method list and page the users searching by a filter series provided by the user
     * request, returning a proper Data Object containing all the pagination metadata and the list
     * of found users in the inner Database
     * @param paginationRequest the page request, contains page size and number
     * @param userFilters the filter series like usco code, full name, etc.
     * @return the proper Data Object that contains the metadata and list of found users
     */
    @Override
    public PageResponse<UserModel> pageUsers(PaginationRequest paginationRequest, UserFilters userFilters) {
        PaginationResult<UserModel> pageResult = userRepository.pageUsers(paginationRequest, userFilters);

        return PageResponse.<UserModel>builder()
                .data(pageResult.getContent())
                .metaData(pageResult.toMetaData())
                .build();
    }

    /**
     * This method is responsible for searching through the DB Repository if there is
     * a user with the inserted <code>userId</code> UUID and return its profile information
     * @param userId the userId UUID to search
     * @return Data object with the found user information
     */
    @Override
    public UserModel findUserById(UUID userId) {
        Optional<UserModel> optionalUser = userRepository.findUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserWithFieldNotExistsException(ID);
        }
        return optionalUser.get();
    }

    @Override
    public UserModel findUserByEmail(String userEmail) {
        Optional<UserModel> optionalUser = userRepository.findUserByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new UserWithFieldNotExistsException(EMAIL);
        }
        return optionalUser.get();
    }

    /**
     * This method contains the business logic to create a user in the Inner DB and stablish the
     * connection with Keycloak via Adapter, also, verify that the introduced Usco Code and Email
     * aren't duplicated.
     * Does not return anything
     * @param user model
     */
    @Override
    public void registerUser(UserModel user) {
        boolean userExistsByUsername = userRepository.existsByFilters(
                UserFilters.builder().username(user.getUsername()).build());

        if (userExistsByUsername) throw new UserWithFieldAlreadyExistsException(USERNAME);

        boolean userExistsByEmail = userRepository.existsByFilters(
                UserFilters.builder().userEmail(user.getUserEmail()).build());

        if (userExistsByEmail) throw new UserWithFieldAlreadyExistsException(EMAIL);

        user.getUserRoles().forEach(roleModel -> {
            verifyIsValidRole(roleModel.getRoleName());
            RoleModel roleToAdd = returnInnerRole(roleModel.getRoleName());
            roleModel.setRoleId(roleToAdd.getRoleId());
        });

        LOG.info("[USER SERVICE] User to be created on Keycloak: {}", user);

        String userId = keycloakConnector.registerKeycloakUser(user);
        user.setUserId(UUID.fromString(userId));

        LOG.info("[USER SERVICE] User to be inserted on Database: {}", user);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            keycloakConnector.rollbackKeycloakUserCreation(userId);
            throw new FailedOperationException(CREATION_OP, DATABASE_ERR_CONNECTION);
        }

    }

    /**
     * This method update the basic user information e.g. <code>uscoCode</code> and <code>fullName</code>
     * in the inner app DB and in the External Service <strong>Keycloak</strong> Database, it validates
     * that at least one profile value are going to be updated, returning an exception otherwise
     * @param toUpdateInfo The new profile info to be updated
     * @param userId The userId to be search and updated
     */
    @Override
    @Transactional
    public void updateUserInformation(UserModel toUpdateInfo, UUID userId) {
        UserModel toUpdateUser = findUserById(userId);

        if (
                toUpdateInfo.getUsername() == null
        )
            throw new FailedOperationException(UPDATE_OP, AT_LEAST_ONE_FIELD_REQUIRED);

        if (toUpdateInfo.getUsername().equals(toUpdateUser.getUsername()))
            throw new SameFieldValueOnUpdateException(USERNAME);
        toUpdateUser.setUsername(toUpdateInfo.getUsername());

        try {
            userRepository.save(toUpdateUser);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        updateOnKeycloak(toUpdateUser);
    }

    @Override
    @Transactional
    public void updateUserEmail(String email, UUID userId) {
        UserModel userToUpdate = findUserById(userId);

        if (userToUpdate.getUserEmail().equals(email))
            throw new SameFieldValueOnUpdateException(EMAIL);
        userToUpdate.setUserEmail(email);

        try {
            userRepository.save(userToUpdate);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        updateOnKeycloak(userToUpdate);
    }

    @Override
    public void updateUserPassword(String newPassword, UUID userId) {
        keycloakConnector.updateKeycloakUserCredentials(userId.toString(), newPassword);
    }

    @Override
    @Transactional
    public void addUserRole(String newRole, UUID userId) {
        UserModel userToAddRole = findUserById(userId);

        verifyIsValidRole(newRole);
        userToAddRole.getUserRoles().forEach(roleModel -> {
            if (roleModel.getRoleName().equals(newRole)) {
                throw new UserAlreadyHaveThatRoleException(newRole);
            }
        });

        RoleModel roleToAdd = returnInnerRole(newRole);
        userToAddRole.getUserRoles().add(roleToAdd);

        try {
            userRepository.save(userToAddRole);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        updateOnKeycloak(userToAddRole);
    }

    @Override
    @Transactional
    public void removeUserRole(String newRole, UUID userId) {
        UserModel userToRemoveRole = findUserById(userId);

        RoleModel roleToRemove = null;
        for (RoleModel roleModel : userToRemoveRole.getUserRoles()) {
            if (roleModel.getRoleName().equals(newRole)) {
                roleToRemove = roleModel;
                break;
            }
        }
        if (roleToRemove == null)
            throw new UserNotHaveThatRoleException(newRole);

        userToRemoveRole.getUserRoles().remove(roleToRemove);

        if (userToRemoveRole.getUserRoles().isEmpty()) {
            throw new UserRolesListCantBeEmptyException();
        }

        try {
            userRepository.save(userToRemoveRole);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        updateOnKeycloak(userToRemoveRole);
    }

    @Override
    @Transactional
    public void toggleUserStatus(UUID userId) {
        UserModel userToUpdate = findUserById(userId);

        userToUpdate.setUserEnabled(!userToUpdate.isUserEnabled());

        try {
            userRepository.save(userToUpdate);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        toggleKeycloakUserStatus(userToUpdate.getUserId().toString());
    }

    private void verifyIsValidRole(String roleModel) {
        if (roleModel.contains(ADMINISTRATOR)) {
            throw new FailedOperationException(CREATION_OP, String.format(USER_WITH_ROLE_INVALID, ADMINISTRATOR));
        }
    }

    private RoleModel returnInnerRole(String roleName) {
        return roleUseCases.findRoleByName(roleName);
    }

    private void updateOnKeycloak(UserModel userToUpdate) {
        try {
            keycloakConnector.updateKeycloakUser(
                    userToUpdate.getUserId().toString(), userToUpdate);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    private void toggleKeycloakUserStatus(String userId) {
        try {
            keycloakConnector.toggleKeycloakUserStatus(userId);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

}
