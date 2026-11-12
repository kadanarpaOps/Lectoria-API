package com.lectoria_api.notifications.infrastructure.output.persistence.mapper;

import com.lectoria_api.notifications.domain.model.union.InAppNotificationModel;
import com.lectoria_api.notifications.infrastructure.output.persistence.entities.SentNotificationEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationPersistenceMapperTest {

    private final NotificationPersistenceMapper mapper = new NotificationPersistenceMapper();

    @Test
    void mapsNotificationUserIdToUuidForPersistence() {
        UUID userId = UUID.randomUUID();
        InAppNotificationModel model = InAppNotificationModel.builder()
                .userId(userId.toString())
                .notificationContent("Test notification")
                .build();

        SentNotificationEntity entity = mapper.toEntity(model);

        assertEquals(userId, entity.getUserId());
    }

    @Test
    void mapsUuidIdentifiersBackToStringDomainValues() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SentNotificationEntity entity = SentNotificationEntity.builder()
                .notificationId(notificationId)
                .userId(userId)
                .notificationContent("Test notification")
                .build();

        InAppNotificationModel model = mapper.toModel(entity);

        assertEquals(notificationId.toString(), model.getNotificationId());
        assertEquals(userId.toString(), model.getUserId());
    }
}
