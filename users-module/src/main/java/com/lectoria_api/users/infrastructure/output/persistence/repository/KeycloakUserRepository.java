package com.lectoria_api.users.infrastructure.output.persistence.repository;

import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.lectoria_api.users.domain.constants.Constants.CREATION_OP;
import static com.lectoria_api.users.domain.constants.Constants.KEYCLOAK_ERR_CONNECTION;

@RequiredArgsConstructor
@Repository
public class KeycloakUserRepository {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.skip.defaultrole}")
    private String skipDefaultRole;

    public String save(UserRepresentation user) {
        String locationHeader;
        try (Response response = keycloak.realm(realm).users().create(user)) {

            locationHeader = response.getHeaderString(HttpHeaders.LOCATION);

            if (locationHeader != null) {
                locationHeader = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
            }

        } catch (RuntimeException e) {
            throw new FailedOperationException(CREATION_OP, KEYCLOAK_ERR_CONNECTION);
        }
        return locationHeader;
    }

    public void update(String userId, UserRepresentation newInfo) {
        keycloak.realm(realm).users().get(userId).update(newInfo);
    }

    public UserRepresentation getById(String userId) {
        return keycloak.realm(realm).users().get(userId).toRepresentation();
    }

    public UserRepresentation getByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(realm)
                .users().searchByUsername(username, true);

        if (users.isEmpty()) {return null;} else {return users.get(0);}
    }

    public UserRepresentation getByEmail(String email) {
        List<UserRepresentation> users = keycloak.realm(realm)
                .users().searchByEmail(email, true);

        if (users.isEmpty()) {return null;} else {return users.get(0);}
    }

    public RoleRepresentation getRoleByName(String roleName) {
        return keycloak.realm(realm).roles().get(roleName).toRepresentation();
    }

    public void assignRole(RoleRepresentation role, String userId) {
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(List.of(role));
    }

    public void removeRole(RoleRepresentation role, String userId) {
        keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(List.of(role));
    }

    public void delete(String userId) {
        keycloak.realm(realm).users().delete(userId);
    }

}
