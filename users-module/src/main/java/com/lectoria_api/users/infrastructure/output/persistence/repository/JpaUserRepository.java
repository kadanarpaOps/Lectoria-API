package com.lectoria_api.users.infrastructure.output.persistence.repository;

import com.lectoria_api.users.infrastructure.output.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(u.username = :username) AND " +
            "(u.userEnabled = :isEnabled)")
    Optional<UserEntity> findAnyUserByUsername(
            @Param("username") String username,
            @Param("isEnabled") Boolean isEnabled
    );

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(u.userEmail = :userEmail) AND " +
            "(u.userEnabled = :isEnabled)")
    Optional<UserEntity> findAnyUserByUserEmail(
            @Param("userEmail") String username,
            @Param("isEnabled") Boolean isEnabled
    );

    boolean existsByUsernameAndUserEnabled(String username, boolean userEnabled);

    boolean existsByUserEmailAndUserEnabled(String userEmail, boolean userEnabled);
}
