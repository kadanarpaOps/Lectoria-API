package com.lectoria_api.users.infrastructure.output.persistence.repository;

import com.lectoria_api.users.infrastructure.output.persistence.entities.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, UUID> {

    @Query(value = "SELECT r FROM RoleEntity r " +
            "WHERE (:roleName IS NULL OR r.roleName ILIKE %:roleName%)"
    )
    Page<RoleEntity> pageRoles(
            @Param("roleName") String roleName,
            Pageable pageable);

    Optional<RoleEntity> findByRoleName(String roleName);
}
