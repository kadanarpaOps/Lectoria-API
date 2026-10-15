package com.lectoria_api.users.infrastructure.output.persistence.repository;

import com.lectoria_api.users.domain.model.UserModel;
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

    Optional<UserModel> findByUsernameOrUserEmail(String username, String userEmail);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :value OR u.userEmail = :value")
    Optional<UserModel> findByUsernameOrUserEmail(@Param("value") String usernameOrEmail);

}
