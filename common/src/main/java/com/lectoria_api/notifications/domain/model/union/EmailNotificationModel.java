package com.lectoria_api.notifications.domain.model.union;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationModel implements NotificationData {

    private String emailReceiver;
    private String notificationTemplateId;
    private Map<String, String> dataValues;

}
