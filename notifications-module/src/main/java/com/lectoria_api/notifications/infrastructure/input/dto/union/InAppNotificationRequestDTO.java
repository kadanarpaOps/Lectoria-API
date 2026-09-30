package com.lectoria_api.notifications.infrastructure.input.dto.union;

import com.lectoria_api.notifications.infrastructure.input.dto.union.base.NotificationDataDTO;
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
public class InAppNotificationRequestDTO implements NotificationDataDTO {

    private String userId;
    private String notificationContent;

}
