package com.lectoria_api.users.infrastructure.output.config.data;

import com.lectoria_api.users.domain.constants.Constants;
import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.model.UserDetails;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.RoleUseCases;
import com.lectoria_api.users.domain.ports.output.KeycloakConnectorPort;
import com.lectoria_api.users.domain.ports.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.lectoria_api.users.domain.constants.Constants.CREATION_OP;
import static com.lectoria_api.users.domain.constants.Constants.DATABASE_ERR_CONNECTION;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final KeycloakConnectorPort keycloakConnector;
    private final UserRepositoryPort userRepository;
    private final RoleUseCases roleService;

    @Value("${data.seeder.admin-username}")
    private String adminUsername;

    @Value("${data.seeder.admin-password}")
    private String adminPassword;

    @Value("${data.seeder.admin-email}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception {
        UserFilters searchAdminFilters = UserFilters.builder()
                .userRolesNames(List.of(Constants.ADMINISTRATOR))
                .build();

        boolean existsAdmin = userRepository.existsByFilters(searchAdminFilters);

        if (!existsAdmin) {
            RoleModel adminRole = roleService.findRoleByName(Constants.ADMINISTRATOR);

            UserDetails userDetails = UserDetails.builder()
                    .registeredVerified(true)
                    .build();

            UserModel adminUser = UserModel.builder()
                    .username(adminUsername)
                    .userEmail(adminEmail)
                    .userPassword(adminPassword)
                    .userRoles(List.of(adminRole))
                    .userDetails(userDetails)
                    .userEnabled(true)
                    .build();

            String userId = keycloakConnector.registerKeycloakUser(adminUser);
            adminUser.setUserId(UUID.fromString(userId));

            try {
                userRepository.save(adminUser);
            } catch (Exception e) {
                keycloakConnector.rollbackKeycloakUserCreation(userId);
                throw new FailedOperationException(CREATION_OP, DATABASE_ERR_CONNECTION);
            }

        }

    }

}
