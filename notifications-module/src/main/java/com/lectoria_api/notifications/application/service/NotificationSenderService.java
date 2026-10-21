package com.lectoria_api.notifications.application.service;

import com.lectoria_api.notifications.application.factory.NotifierFactory;
import com.lectoria_api.notifications.application.strategies.Notifier;
import com.lectoria_api.notifications.domain.model.NotificationModel;
import com.lectoria_api.notifications.domain.ports.input.NotifierUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSenderService implements NotifierUseCases {

    private final NotifierFactory notifierFactory;

    @Override
    public void executeSendNotification(NotificationModel notification) {
        Notifier notifier = notifierFactory.getNotifier(notification.getNotificationType());
        notifier.sendNotification(notification.getNotificationData());
    }

}
