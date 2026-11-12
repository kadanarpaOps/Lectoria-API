package com.lectoria_api.notifications.infrastructure.output.persistence.repository;

import com.lectoria_api.notifications.infrastructure.output.persistence.entities.SentNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaSentNotificationRepository extends JpaRepository<SentNotificationEntity, UUID> {

    List<SentNotificationEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Integer countByShownEqualsAndUserIdEquals(boolean shown, UUID userId);
    Page<SentNotificationEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Optional<SentNotificationEntity> findByNotificationIdAndUserId(UUID notificationId, UUID userId);
}
