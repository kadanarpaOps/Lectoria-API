package com.lectoria_api.notifications.infrastructure.input.mapper;

import com.lectoria_api.notifications.domain.exceptions.business.NotFoundNotifierException;
import com.lectoria_api.notifications.domain.model.NotificationModel;
import com.lectoria_api.notifications.domain.model.union.EmailNotificationModel;
import com.lectoria_api.notifications.domain.model.union.InAppNotificationModel;
import com.lectoria_api.notifications.domain.model.union.NotificationData;
import com.lectoria_api.notifications.infrastructure.input.dto.NotificationRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.union.EmailNotificationRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.union.InAppNotificationRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.union.base.NotificationDataDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationInputMapper {

    public NotificationModel toModel(NotificationRequestDTO notificationDTO) {
        if (notificationDTO == null) return null;
        return NotificationModel.builder()
                .notificationType(notificationDTO.getNotificationType())
                .notificationData(map(notificationDTO.getNotificationData()))
                .build();
    }

    public EmailNotificationModel toEmailModel(EmailNotificationRequestDTO notificationDTO) {
        if (notificationDTO == null) return null;
        return EmailNotificationModel.builder()
                .emailReceiver(notificationDTO.getEmailReceiver())
                .notificationTemplateId(notificationDTO.getNotificationTemplateId())
                .dataValues(notificationDTO.getDataValues())
                .build();
    }

    public InAppNotificationModel toInAppModel(InAppNotificationRequestDTO notificationDTO) {
        if (notificationDTO == null) return null;
        return InAppNotificationModel.builder()
                .userId(notificationDTO.getUserId())
                .notificationContent(notificationDTO.getNotificationContent())
                .build();
    }

    public NotificationData map(NotificationDataDTO notificationDataDTO) {
        if (notificationDataDTO == null) return null;
        if (notificationDataDTO instanceof EmailNotificationRequestDTO email) {
            return toEmailModel(email);
        }
        if (notificationDataDTO instanceof InAppNotificationRequestDTO inApp) {
            return toInAppModel(inApp);
        }
        throw new NotFoundNotifierException(notificationDataDTO.getClass().getSimpleName());
    }

}
