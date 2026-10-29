package com.lectoria_api.stories.infrastructure.output.persistence.repository;

import com.lectoria_api.stories.infrastructure.output.persistence.entities.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRatingRepository extends JpaRepository<RatingEntity, UUID> {
    Optional<RatingEntity> findByStoryIdAndUserId(UUID storyId, UUID userId);
    long countByStoryId(UUID storyId);
    @Query("SELECT COALESCE(SUM(r.score), 0) FROM RatingEntity r WHERE r.storyId = :storyId")
    java.math.BigDecimal sumScoreByStoryId(@Param("storyId") UUID storyId);
    @Query("SELECT COALESCE(AVG(r.score), 0) FROM RatingEntity r WHERE r.storyId = :storyId")
    java.math.BigDecimal averageScoreByStoryId(@Param("storyId") UUID storyId);
}
