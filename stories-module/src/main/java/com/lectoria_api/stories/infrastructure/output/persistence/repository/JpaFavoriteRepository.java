package com.lectoria_api.stories.infrastructure.output.persistence.repository;

import com.lectoria_api.stories.infrastructure.output.persistence.entities.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaFavoriteRepository extends JpaRepository<FavoriteEntity, UUID> {
    Optional<FavoriteEntity> findByStoryIdAndUserId(UUID storyId, UUID userId);
    Page<FavoriteEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByStoryId(UUID storyId);
    long countByUserId(UUID userId);
}
