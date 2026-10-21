package com.lectoria_api.users.infrastructure.output.persistence.specification;

import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.infrastructure.output.persistence.entities.RoleEntity;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserDetailsEntity;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    private UserSpecification() {
        /* Utility Class, should not be instantiated */
    }

    public static Specification<UserEntity> withFilters(UserFilters filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getUsername() != null && !filters.getUsername().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + filters.getUsername().toLowerCase() + "%"));
            }

            if (filters.getUserEmail() != null && !filters.getUserEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("userEmail")), "%" + filters.getUserEmail().toLowerCase() + "%"));
            }

            if (filters.getUserStatus() != null) {
                predicates.add(cb.equal(root.get("userEnabled"), filters.getUserStatus()));
            }

            if (filters.getIsRegistryVerified() != null) {
                Join<UserEntity, UserDetailsEntity> joinDetails = root.join("userDetails");
                predicates.add(cb.equal(joinDetails.get("registeredVerified"), filters.getIsRegistryVerified()));
            }

            if (filters.getUserRolesNames() != null && !filters.getUserRolesNames().isEmpty()) {
                Join<UserEntity, RoleEntity> joinRoles = root.join("userRoles");
                predicates.add(joinRoles.get("roleName").in(filters.getUserRolesNames()));
            }

            if (filters.getExcludedRolesNames() != null && !filters.getExcludedRolesNames().isEmpty()) {
                Join<UserEntity, RoleEntity> joinRoles = root.join("userRoles");
                predicates.add(cb.not(joinRoles.get("roleName").in(filters.getExcludedRolesNames())));
            }

            if (filters.getExcludedUsers() != null && !filters.getExcludedUsers().isEmpty()) {
                predicates.add(cb.not(root.get("userId").in(filters.getExcludedUsers())));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
