package com.lectoria_api.stories.infrastructure.output.persistence.repository;

import com.lectoria_api.stories.infrastructure.output.persistence.entities.StoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaStoryRepository extends JpaRepository<StoryEntity, UUID>, JpaSpecificationExecutor<StoryEntity> {
    Optional<StoryEntity> findByTitleIgnoreCase(String title);
    boolean existsByTitleIgnoreCase(String title);
    boolean existsByTitleIgnoreCaseAndIdNot(String title, UUID id);
    Optional<StoryEntity> findFirstByUserIdOrderByLastActivityAtDescCreatedAtDesc(UUID userId);
    long countByUserId(UUID userId);
    List<StoryEntity> findByIdIn(List<UUID> ids);

    @Query("SELECT s FROM StoryEntity s JOIN FavoriteEntity f ON f.storyId = s.id " +
            "WHERE f.userId = :userId ORDER BY f.createdAt DESC")
    Page<StoryEntity> findFavoritesByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT s FROM StoryEntity s WHERE " +
            "(:ownerId IS NULL OR s.userId = :ownerId) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:hidden IS NULL OR s.hidden = :hidden)")
    Page<StoryEntity> findVisibleStories(@Param("ownerId") UUID ownerId,
                                         @Param("status") String status,
                                         @Param("hidden") Boolean hidden,
                                         Pageable pageable);
}
