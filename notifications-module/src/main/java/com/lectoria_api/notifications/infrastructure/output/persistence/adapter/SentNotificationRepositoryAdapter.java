package com.lectoria_api.notifications.infrastructure.output.persistence.adapter;

import com.lectoria_api.notifications.domain.model.union.InAppNotificationModel;
import com.lectoria_api.notifications.domain.ports.output.SentNotificationRepositoryPort;
import com.lectoria_api.notifications.infrastructure.output.persistence.mapper.NotificationPersistenceMapper;
import com.lectoria_api.notifications.infrastructure.output.persistence.repository.JpaSentNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SentNotificationRepositoryAdapter implements SentNotificationRepositoryPort {

    private final JpaSentNotificationRepository notificationRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public void saveSentNotification(InAppNotificationModel notification) {
        notificationRepository.save(mapper.toEntity(notification));
    }

    @Override
    public List<InAppNotificationModel> findNotificationByUserId(String userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(mapper::toModel).toList();
    }

    @Override
    public Integer countByShownState(boolean shown, String userId) {
        return notificationRepository.countByShownEqualsAndUserIdEquals(shown, userId);
    }

}
