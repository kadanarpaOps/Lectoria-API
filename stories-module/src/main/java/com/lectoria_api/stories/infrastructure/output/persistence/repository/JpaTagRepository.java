package com.lectoria_api.stories.infrastructure.output.persistence.repository;

import com.lectoria_api.stories.infrastructure.output.persistence.entities.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTagRepository extends JpaRepository<TagEntity, UUID> {
    Page<TagEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<TagEntity> findByNameIgnoreCase(String name);
    List<TagEntity> findByNameIn(List<String> names);
}
