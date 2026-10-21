package com.lectoria_api.notifications.domain.ports.output;

import com.lectoria_api.notifications.domain.model.union.InAppNotificationModel;

import java.util.List;

public interface SentNotificationRepositoryPort {

    void saveSentNotification(InAppNotificationModel notification);

    List<InAppNotificationModel> findNotificationByUserId(String userId);

    Integer countByShownState(boolean shown, String userId);

}
