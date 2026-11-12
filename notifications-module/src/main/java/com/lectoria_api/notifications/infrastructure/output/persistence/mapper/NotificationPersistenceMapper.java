package com.lectoria_api.notifications.infrastructure.output.persistence.mapper;

import com.lectoria_api.notifications.domain.model.EmailTemplateModel;
import com.lectoria_api.notifications.domain.model.union.InAppNotificationModel;
import com.lectoria_api.notifications.infrastructure.output.persistence.entities.EmailTemplateEntity;
import com.lectoria_api.notifications.infrastructure.output.persistence.entities.SentNotificationEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationPersistenceMapper {

    public EmailTemplateEntity toEntity(EmailTemplateModel emailTemplateModel) {
        if (emailTemplateModel == null) return null;
        return EmailTemplateEntity.builder()
                .notificationTemplateId(emailTemplateModel.getNotificationTemplateId())
                .templateName(emailTemplateModel.getTemplateName())
                .templateSubject(emailTemplateModel.getTemplateSubject())
                .templateLocation(emailTemplateModel.getTemplateLocation())
                .createdAt(emailTemplateModel.getCreatedAt())
                .updatedAt(emailTemplateModel.getUpdatedAt())
                .build();
    }

    public SentNotificationEntity toEntity(InAppNotificationModel inAppNotificationModel) {
        if (inAppNotificationModel == null) return null;
        return SentNotificationEntity.builder()
                .notificationId(parseUuid(inAppNotificationModel.getNotificationId()))
                .userId(UUID.fromString(inAppNotificationModel.getUserId()))
                .notificationContent(inAppNotificationModel.getNotificationContent())
                .shown(inAppNotificationModel.isShown())
                .createdAt(inAppNotificationModel.getCreatedAt())
                .shownAt(inAppNotificationModel.getShownAt())
                .build();
    }

    public EmailTemplateModel toModel(EmailTemplateEntity emailTemplateEntity) {
        if (emailTemplateEntity == null) return null;
        return EmailTemplateModel.builder()
                .notificationTemplateId(emailTemplateEntity.getNotificationTemplateId())
                .templateName(emailTemplateEntity.getTemplateName())
                .templateSubject(emailTemplateEntity.getTemplateSubject())
                .templateLocation(emailTemplateEntity.getTemplateLocation())
                .createdAt(emailTemplateEntity.getCreatedAt())
                .updatedAt(emailTemplateEntity.getUpdatedAt())
                .build();
    }

    public InAppNotificationModel toModel(SentNotificationEntity sentNotificationEntity) {
        if (sentNotificationEntity == null) return null;
        return InAppNotificationModel.builder()
                .notificationId(sentNotificationEntity.getNotificationId() == null
                        ? null : sentNotificationEntity.getNotificationId().toString())
                .userId(sentNotificationEntity.getUserId().toString())
                .notificationContent(sentNotificationEntity.getNotificationContent())
                .shown(sentNotificationEntity.isShown())
                .createdAt(sentNotificationEntity.getCreatedAt())
                .shownAt(sentNotificationEntity.getShownAt())
                .build();
    }

    private UUID parseUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    public List<EmailTemplateModel> toModel(List<EmailTemplateEntity> emailTemplateEntities) {
        if (emailTemplateEntities == null) return Collections.emptyList();
        return emailTemplateEntities.stream().map(this::toModel).toList();
    }

}
