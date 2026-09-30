package com.lectoria_api.notifications.domain.ports.input;

import com.lectoria_api.notifications.domain.model.NotificationModel;

public interface NotifierUseCases {

    void executeSendNotification(NotificationModel notification);

}
