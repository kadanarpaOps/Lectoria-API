package com.lectoria_api.lectoria_api.infrastructure.config.converter;

import com.lectoria_api.lectoria_api.domain.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Lazy
public class KeycloakAuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public Collection<GrantedAuthority> convert(Jwt token) {
        Map<String, Object> realmAccess = (Map<String, Object>) token.getClaims().get(Constants.REALM_ACCESS);

        if (realmAccess == null || !realmAccess.containsKey(Constants.ROLES))
            return Collections.emptyList();

        List<String> roles = (List<String>) realmAccess.get(Constants.ROLES);
        Collection<GrantedAuthority> userRoles = new ArrayList<>();

        roles.stream().filter(role -> !role.equals(Constants.DEFAULT_ROLES.concat(realm.toLowerCase())))
                .forEach(role -> userRoles.add(new SimpleGrantedAuthority(Constants.ROLE.concat(role))));

        return userRoles;
    }

}
