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
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldAlreadyExistsException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.model.UserDetails;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.RoleUseCases;
import com.lectoria_api.users.domain.ports.input.UserUseCases;
import com.lectoria_api.users.domain.ports.output.UserRepositoryPort;
import com.lectoria_api.users.domain.ports.output.KeycloakConnectorPort;
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
public class UserServicePort implements UserUseCases {

    private final UserRepositoryPort userRepository;
    private final KeycloakConnectorPort keycloakConnector;
    private final RoleUseCases roleService;
    private final AuthUseCases authService;
    private final SharedVerificationUseCases verificationService;
    private final NotificationBuilderUtil notificationBuilderUtil;

    @Lazy
    private final UserServicePort self;

    private static final Logger LOG = LoggerFactory.getLogger(UserServicePort.class);

    /**
     * This method list and page the users searching by a filter series provided by the user
     * request, returning a proper Data Object containing all the pagination metadata and the list
     * of found users in the inner Database
     * @param paginationRequest the page request, contains page size and number
     * @param userFilters the filter series like username, email, etc.
     * @return the proper Data Object that contains the metadata and list of found users
     */
    @Override
    public PageResponse<UserModel> pageUsers(PaginationRequest paginationRequest, UserFilters userFilters) {
        userFilters.setUserStatus(true);
        userFilters.setIsRegistryVerified(true);
        userFilters.setExcludedUsers(List.of(authService.getAuthUserId()));
        userFilters.setExcludedRolesNames(List.of(ADMINISTRATOR));

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
    public UserModel findAnyUserByUsername(String username, Boolean isEnabled) {
        Optional<UserModel> optionalUser = userRepository.findAnyUserByUsername(username, isEnabled);
        if (optionalUser.isEmpty()) {
            throw new UserWithFieldNotExistsException(USERNAME);
        }
        return optionalUser.get();
    }

    @Override
    public UserModel findAnyUserByUserEmail(String userEmail, Boolean isEnabled) {
        Optional<UserModel> optionalUser = userRepository.findAnyUserByUserEmail(userEmail, isEnabled);
        if (optionalUser.isEmpty()) {
            throw new UserWithFieldNotExistsException(EMAIL);
        }
        return optionalUser.get();
    }

    @Override
    public boolean existsUserByUserEmail(String userEmail, Boolean isEnabled) {
        return userRepository.existsByEmailAndStatus(userEmail, isEnabled);
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

        resolveUserAvailability(user);

        user.getUserRoles().forEach(roleModel -> {
            verifyIsValidRole(roleModel.getRoleName());
            RoleModel roleToAdd = returnInnerRole(roleModel.getRoleName());
            roleModel.setRoleId(roleToAdd.getRoleId());
        });

        UserDetails userDetails = UserDetails.builder()
                .registeredVerified(false)
                .build();

        user.setUserEnabled(false);
        user.setUserDetails(userDetails);

        LOG.info("[USER SERVICE] User to be created on Keycloak: {}", user);

        String userId = keycloakConnector.registerKeycloakUser(user);
        user.setUserId(UUID.fromString(userId));

        LOG.info("[USER SERVICE] User to be inserted on Database: {}", user);

        try {
            userRepository.save(user);
            createRegistryVerification(user);
        } catch (Exception e) {
            keycloakConnector.rollbackKeycloakUserCreation(userId);
            throw new FailedOperationException(CREATION_OP, DATABASE_ERR_CONNECTION);
        }
    }

    @Override
    @Transactional
    public void verifyRegistry(String username, String email) {
        UserModel foundUser = resolveUserFromPublicScope(username, email);

        validateVerification(foundUser.getUserId(), VerificationType.VERIFY_REGISTRY);

        foundUser.setUserEnabled(true);
        foundUser.getUserDetails().setRegisteredVerified(true);

        try {
            userRepository.save(foundUser);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
        updateOnKeycloak(foundUser);

        notificationBuilderUtil.sendSuccessVerifyNotification(foundUser.getUsername(), foundUser.getUserEmail());
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

        if (userRepository.existsByUsernameAndStatus(toUpdateInfo.getUsername(), true)) {
            throw new UserWithFieldAlreadyExistsException(USERNAME);
        }

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
    public void updateUserProfileImage(String profileImageUrl, UUID userId) {
        UserModel user = findUserById(userId);
        if (user.getUserDetails() == null) {
            user.setUserDetails(UserDetails.builder().build());
        }
        user.getUserDetails().setProfileImageUrl(profileImageUrl);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, DATABASE_ERR_CONNECTION);
        }
    }

    @Override
    @Transactional
    public void updateUserEmail(String email, UUID userId) {
        UserModel userToUpdate = findUserById(userId);

        if (userToUpdate.getUserEmail().equals(email))
            throw new SameFieldValueOnUpdateException(EMAIL);

        if (userRepository.existsByEmailAndStatus(email, true)) {
            throw new UserWithFieldAlreadyExistsException(EMAIL);
        }

        validateVerification(userToUpdate.getUserId(), VerificationType.CHANGE_EMAIL);
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
        validateVerification(userId, VerificationType.CHANGE_PASSWORD);
        keycloakConnector.updateKeycloakUserCredentials(userId.toString(), newPassword);
    }

    @Override
    public void resetUserPassword(String username, String email, String newPassword) {
        UserModel user = resolveUserFromPublicScope(username, email, true);
        validateVerification(user.getUserId(), VerificationType.FORGOT_PASSWORD);
        keycloakConnector.updateKeycloakUserCredentials(user.getUserId().toString(), newPassword);
    }

    @Override
    @Transactional
    public void disableUserAccount(UUID userId) {
        validateVerification(userId, VerificationType.DISABLE_ACCOUNT);
        self.toggleUserStatus(userId);
    }

    @Override
    @Transactional
    public void reactivateUserAccount(String username, String email, String newPassword) {
        UserModel foundUser = resolveUserFromPublicScope(username, email);

        validateVerification(foundUser.getUserId(), VerificationType.REACTIVATE_ACCOUNT);
        keycloakConnector.updateKeycloakUserCredentials(foundUser.toString(), newPassword);
        self.toggleUserStatus(foundUser.getUserId());
    }

    private void resolveUserAvailability(UserModel user) {

        validateEnabledUserAbsence(user);

        userRepository.findAnyUsersByUsername(user.getUsername(), false)
                .forEach(disabledUser ->
                        handleUnverifiedDisabledUser(disabledUser, true)
                );

        userRepository.findAnyUsersByUserEmail(user.getUserEmail(), false)
                .forEach(disabledUser ->
                        handleUnverifiedDisabledUser(disabledUser, false)
                );

    }

    private void validateEnabledUserAbsence(UserModel user) {
        if(userRepository.existsByUsernameAndStatus(user.getUsername(), true)) {
            throw new UserWithFieldAlreadyExistsException(USERNAME);
        }

        if(userRepository.existsByEmailAndStatus(user.getUserEmail(), true)) {
            throw new UserWithFieldAlreadyExistsException(EMAIL);
        }
    }

    private void handleUnverifiedDisabledUser(UserModel disabledUser, boolean searchingByUsername) {
        VerificationModel foundVerification;
        if (disabledUser.getUserDetails().isRegisteredVerified()) {
            foundVerification = verificationService.verifyVerificationExistence(
                    disabledUser,
                    VerificationType.REACTIVATE_ACCOUNT
            );
        } else {
            foundVerification = verificationService.verifyVerificationExistence(
                    disabledUser,
                    VerificationType.VERIFY_REGISTRY
            );
            if (foundVerification == null) {
                userRepository.delete(disabledUser.getUserId());
                keycloakConnector.rollbackKeycloakUserCreation(disabledUser.getUserId().toString());
            }
        }

        if (foundVerification != null)
            throw new UserWithFieldAlreadyExistsException(searchingByUsername ? USERNAME : EMAIL);
    }

    private void createRegistryVerification(UserModel user) {
        verificationService.createVerification(user, VerificationType.VERIFY_REGISTRY);
    }

    private void validateVerification(UUID userId, VerificationType verificationType) {
        verificationService
                .existsSuccessVerificationByUserIdAndVerificationType(userId, verificationType);
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

    @Transactional
    public void toggleUserStatus(UUID userId) {
        UserModel userToUpdate = findUserById(userId);
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

    private void toggleKeycloakUserStatus(String userId, UserModel user) {
        try {
            keycloakConnector.toggleKeycloakUserStatus(userId, user);
        } catch (Exception e) {
            throw new FailedOperationException(UPDATE_OP, KEYCLOAK_ERR_CONNECTION);
        }
    }

    private UserModel resolveUserFromPublicScope(String username, String email) {
        return resolveUserFromPublicScope(username, email, false);
    }

    private UserModel resolveUserFromPublicScope(String username, String email, boolean enabled) {
        UserModel foundUser = new UserModel();

        boolean searchByUsername = username != null && !username.isEmpty();
        boolean searchByEmail = email != null && !email.isEmpty();

        if (searchByUsername == searchByEmail)
            throw new InvalidParametersForUserSearchException();
        if (searchByUsername)
            foundUser = findAnyUserByUsername(username, enabled);
        if (searchByEmail)
            foundUser = findAnyUserByUserEmail(email, enabled);

        return foundUser;
    }

}
