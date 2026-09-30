package com.lectoria_api.notifications.infrastructure.input.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lectoria_api.notifications.infrastructure.input.dto.union.EmailNotificationRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.union.InAppNotificationRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.union.base.NotificationDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.lectoria_api.notifications.domain.constants.NotificationTypes.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {

    private String notificationType;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "notificationType"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = EmailNotificationRequestDTO.class, name = email),
            @JsonSubTypes.Type(value = InAppNotificationRequestDTO.class, name = inApp)
    })
    private NotificationDataDTO notificationData;

}
