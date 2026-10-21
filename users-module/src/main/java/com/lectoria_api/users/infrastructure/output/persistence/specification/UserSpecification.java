package com.lectoria_api.users.infrastructure.output.persistence.specification;

import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.infrastructure.output.persistence.entities.RoleEntity;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserDetailsEntity;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserSpecification {

    private UserSpecification() {
        /* Utility Class, should not be instantiated */
    }

    public static Specification<UserEntity> withFilters(UserFilters filters) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType()) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            addStringLikePredicate(predicates, cb, root.get("username"), filters.getUsername());
            addStringLikePredicate(predicates, cb, root.get("userEmail"), filters.getUserEmail());
            addEqualPredicate(predicates, cb, root.get("userEnabled"), filters.getUserStatus());
            addDetailsPredicate(predicates, cb, root, filters.getIsRegistryVerified());
            addRolesPredicate(predicates, cb, root, filters.getUserRolesNames(), false);
            addRolesPredicate(predicates, cb, root, filters.getExcludedRolesNames(), true);
            addExcludedUsersPredicate(predicates, cb, root, filters.getExcludedUsers());

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addStringLikePredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                               jakarta.persistence.criteria.Path<String> path, String value) {
        if (hasText(value)) {
            predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    private static void addEqualPredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                          jakarta.persistence.criteria.Path<Object> path, Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    private static void addDetailsPredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                            Root<UserEntity> root, Boolean isVerified) {
        if (isVerified != null) {
            Join<UserEntity, UserDetailsEntity> joinDetails = root.join("userDetails");
            predicates.add(cb.equal(joinDetails.get("registeredVerified"), isVerified));
        }
    }

    private static void addRolesPredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                          Root<UserEntity> root, List<String> roles, boolean exclude) {
        if (hasElements(roles)) {
            Join<UserEntity, RoleEntity> joinRoles = root.join("userRoles");
            Predicate inPredicate = joinRoles.get("roleName").in(roles);
            predicates.add(exclude ? cb.not(inPredicate) : inPredicate);
        }
    }

    private static void addExcludedUsersPredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                                  Root<UserEntity> root, List<?> excludedUsers) {
        if (hasElements(excludedUsers)) {
            predicates.add(cb.not(root.get("userId").in(excludedUsers)));
        }
    }

    private static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private static boolean hasElements(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

}
