package com.lectoria_api.notifications.application.strategies;

import com.lectoria_api.notifications.domain.model.union.NotificationData;

public interface Notifier {

    void sendNotification(NotificationData notification);

}
