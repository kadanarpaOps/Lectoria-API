package com.lectoria_api.notifications.infrastructure.output.persistence.repository;

import com.lectoria_api.notifications.infrastructure.output.persistence.entities.SentNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JpaSentNotificationRepository extends JpaRepository<SentNotificationEntity, String> {

    List<SentNotificationEntity> findAllByUserIdOrderByCreatedAtDesc(String userId);

    Integer countByShownEqualsAndUserIdEquals(boolean shown, String userId);
    Page<SentNotificationEntity> findAllByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    java.util.Optional<SentNotificationEntity> findByNotificationIdAndUserId(String notificationId, String userId);
}
