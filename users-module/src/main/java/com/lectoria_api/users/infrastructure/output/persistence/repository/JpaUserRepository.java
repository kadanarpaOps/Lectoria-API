package com.lectoria_api.users.infrastructure.output.persistence.repository;

import com.lectoria_api.users.infrastructure.output.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUserEmail(String userEmail);

}
