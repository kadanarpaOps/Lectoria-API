package com.lectoria_api.notifications.domain.model;

import com.lectoria_api.notifications.domain.model.union.NotificationData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationModel {

    private String notificationType;
    private NotificationData notificationData;

}
