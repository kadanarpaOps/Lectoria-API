package com.lectoria_api.users.application.service;

import com.lectoria_api.auth.domain.ports.input.AuthUseCases;
import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.notifications.application.utils.NotificationBuilderUtil;
import com.lectoria_api.users.domain.exceptions.InvalidParametersForUserSearchException;
import com.lectoria_api.users.domain.exceptions.business.AdminCantBeDisabledException;
import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import com.lectoria_api.users.domain.exceptions.business.SameFieldValueOnUpdateException;
import com.lectoria_api.users.domain.exceptions.business.UserAlreadyHaveThatRoleException;
import com.lectoria_api.users.domain.exceptions.business.UserNotHaveThatRoleException;
import com.lectoria_api.users.domain.exceptions.business.UserRolesListCantBeEmptyException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldAlreadyExistsException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.model.UserDetails;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.AdminUseCases;
import com.lectoria_api.users.domain.ports.input.RoleUseCases;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import com.lectoria_api.users.domain.ports.output.KeycloakConnectorPort;
import com.lectoria_api.users.domain.ports.output.UserRepositoryPort;
import com.lectoria_api.verifications.domain.model.VerificationModel;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import com.lectoria_api.verifications.domain.ports.input.SharedVerificationUseCases;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class AdminServicePort implements AdminUseCases {

    private final UserRepositoryPort userRepository;
    private final KeycloakConnectorPort keycloakConnector;
    private final RoleUseCases roleService;
    private final AuthUseCases authService;
    private final UserUseCases userService;
    private final SharedVerificationUseCases verificationService;

    @Lazy
    private final AdminServicePort self;

    @Override
    @Transactional
    public void addUserRole(String newRole, UUID userId) {
        UserModel userToAddRole = userService.findUserById(userId);

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
        keycloakConnector.modifyKeycloakUserRole(userToAddRole.getUserId().toString(), newRole, true);
    }

    @Override
    @Transactional
    public void removeUserRole(String removeRole, UUID userId) {
        UserModel userToRemoveRole = userService.findUserById(userId);

        RoleModel roleToRemove = null;
        for (RoleModel roleModel : userToRemoveRole.getUserRoles()) {
            if (roleModel.getRoleName().equals(removeRole)) {
                roleToRemove = roleModel;
                break;
            }
        }

        if (roleToRemove == null)
            throw new UserNotHaveThatRoleException(removeRole);

        if (userToRemoveRole.isUserEnabled() && removeRole.equals(ADMINISTRATOR))
            throw new AdminCantBeDisabledException();

        userToRemoveRole.getUserRoles().remove(roleToRemove);

        if (userToRemoveRole.getUserRoles().isEmpty()) {
            throw new UserRolesListCantBeEmptyException();
        }

        try {
            userRepository.save(userToRemoveRole);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        keycloakConnector.modifyKeycloakUserRole(userToRemoveRole.getUserId().toString(), removeRole, false);
    }

    @Override
    @Transactional
    public void toggleUserStatus(UUID userId) {
        UserModel userToUpdate = userService.findUserById(userId);
        List<RoleModel> userRoles = userToUpdate.getUserRoles();

        if (userToUpdate.isUserEnabled()) {
            userRoles.forEach(roleModel -> {
                if (roleModel.getRoleName().equals(ADMINISTRATOR)) {
                    throw new AdminCantBeDisabledException();
                }
            });
        }

        userToUpdate.setUserEnabled(!userToUpdate.isUserEnabled());

        try {
            userRepository.save(userToUpdate);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        toggleKeycloakUserStatus(userToUpdate.getUserId().toString(), userToUpdate);
    }

    private void verifyIsValidRole(String roleModel) {
        if (roleModel.contains(ADMINISTRATOR)) {
            throw new FailedOperationException(CREATION_OP, String.format(USER_WITH_ROLE_INVALID, ADMINISTRATOR));
        }
    }

    private RoleModel returnInnerRole(String roleName) {
        return roleService.findRoleByName(roleName);
    }

    private void updateOnKeycloak(UserModel userToUpdate) {
        try {
            keycloakConnector.updateKeycloakUser(
                    userToUpdate.getUserId().toString(), userToUpdate);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    private void toggleKeycloakUserStatus(String userId, UserModel user) {
        try {
            keycloakConnector.toggleKeycloakUserStatus(userId, user);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

}
