package com.lectoria_api.notifications.domain.ports.output;

import com.lectoria_api.notifications.domain.model.union.EmailNotificationModel;

public interface MailSenderPort {

    void executeSendEmail(EmailNotificationModel notification);

}
