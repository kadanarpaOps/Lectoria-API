package com.lectoria_api.stories.infrastructure.output.persistence.repository;

import com.lectoria_api.stories.infrastructure.output.persistence.entities.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaGenreRepository extends JpaRepository<GenreEntity, UUID> {
    Optional<GenreEntity> findByNameIgnoreCase(String name);
}
