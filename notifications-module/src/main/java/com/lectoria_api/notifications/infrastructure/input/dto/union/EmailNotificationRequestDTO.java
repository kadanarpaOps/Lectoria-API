package com.lectoria_api.notifications.infrastructure.input.dto.union;

import com.lectoria_api.notifications.infrastructure.input.dto.union.base.NotificationDataDTO;
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
public class EmailNotificationRequestDTO implements NotificationDataDTO {

    private String emailReceiver;
    private String notificationTemplateId;
    private Map<String, String> dataValues;

}
