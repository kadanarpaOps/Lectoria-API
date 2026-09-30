package com.lectoria_api.notifications.application.strategies;

import com.lectoria_api.notifications.domain.model.union.InAppNotificationModel;
import com.lectoria_api.notifications.domain.model.union.NotificationData;
import com.lectoria_api.notifications.domain.ports.output.SentNotificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.lectoria_api.notifications.domain.constants.NotificationTypes.IN_APP;

@Service(IN_APP)
@RequiredArgsConstructor
public class ApplicationNotifier implements Notifier {

    private final SentNotificationRepositoryPort notificationRepository;

    @Override
    public void sendNotification(NotificationData notification) {
        notificationRepository.saveSentNotification((InAppNotificationModel) notification);
    }

}
