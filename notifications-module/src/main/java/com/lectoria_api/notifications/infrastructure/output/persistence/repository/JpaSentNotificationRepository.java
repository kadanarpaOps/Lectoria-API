package com.lectoria_api.notifications.infrastructure.output.persistence.repository;

import com.lectoria_api.notifications.infrastructure.output.persistence.entities.SentNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSentNotificationRepository extends JpaRepository<SentNotificationEntity, String> {

    List<SentNotificationEntity> findAllByUserIdOrderByCreatedAtDesc(String userId);

    Integer countByShownEqualsAndUserIdEquals(boolean shown, String userId);

}
