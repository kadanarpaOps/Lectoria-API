package com.lectoria_api.notifications.application.utils;

import com.lectoria_api.notifications.domain.model.NotificationModel;
import com.lectoria_api.notifications.domain.model.union.EmailNotificationModel;
import com.lectoria_api.notifications.domain.ports.input.NotifierUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.lectoria_api.notifications.domain.constants.NotificationTemplates.OTP_VERIFICATION;
import static com.lectoria_api.notifications.domain.constants.NotificationTemplates.SUCCESS_REGISTRY;
import static com.lectoria_api.notifications.domain.constants.NotificationTypes.EMAIL;

@Component
@RequiredArgsConstructor
public class NotificationBuilderUtil {

    private final NotifierUseCases notifierService;

    public void sendSuccessVerifyNotification(String username, String receiverEmail) {
        processEmailNotification(
                SUCCESS_REGISTRY,
                Map.of("username", username),
                receiverEmail
        );
    }

    public void sendOtpNotification(String username, String otp, String expiresMinutes, String receiverEmail) {
        processEmailNotification(
                OTP_VERIFICATION,
                Map.of(
                        "username", username,
                        "otpCode", otp,
                        "expiresIn", expiresMinutes
                ),
                receiverEmail
        );
    }

    private void processEmailNotification(
            String templateName,
            Map<String, String> values,
            String receiverEmail
    ) {
        EmailNotificationModel notificationData = EmailNotificationModel.builder()
                .notificationTemplateId(templateName)
                .dataValues(values)
                .emailReceiver(receiverEmail)
                .build();

        NotificationModel notification = NotificationModel.builder()
                .notificationType(EMAIL)
                .notificationData(notificationData)
                .build();

        notifierService.executeSendNotification(notification);
    }

}
