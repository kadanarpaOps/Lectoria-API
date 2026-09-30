package com.lectoria_api.notifications.application.strategies;

import com.lectoria_api.notifications.domain.model.union.EmailNotificationModel;
import com.lectoria_api.notifications.domain.model.union.NotificationData;
import com.lectoria_api.notifications.domain.ports.output.MailSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.lectoria_api.notifications.domain.constants.NotificationTypes.email;

@Service(email)
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {

    private final MailSenderPort mailSender;

    @Override
    public void sendNotification(NotificationData notification) {
        mailSender.executeSendEmail((EmailNotificationModel) notification);
    }

}
