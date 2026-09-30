package com.lectoria_api.notifications.infrastructure.output.mail.adapter;

import com.lectoria_api.notifications.domain.exceptions.business.FailedMailSenderOperation;
import com.lectoria_api.notifications.domain.exceptions.business.NotFoundTemplateException;
import com.lectoria_api.notifications.domain.model.EmailTemplateModel;
import com.lectoria_api.notifications.domain.model.union.EmailNotificationModel;
import com.lectoria_api.notifications.domain.ports.output.EmailTemplateRepositoryPort;
import com.lectoria_api.notifications.domain.ports.output.MailSenderPort;
import com.lectoria_api.notifications.infrastructure.output.mail.resolver.TemplateResolver;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSenderAdapter implements MailSenderPort {

    private final EmailTemplateRepositoryPort templateRepository;
    private final TemplateResolver templateResolver;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void executeSendEmail(EmailNotificationModel notification) {
        String notificationTemplateId = notification.getNotificationTemplateId();
        EmailTemplateModel template = templateRepository.findTemplateByName(
                notificationTemplateId).orElseThrow(() -> new NotFoundTemplateException(notificationTemplateId));

        String templateContent = templateResolver.resolve(
                template.getTemplateLocation(),
                notification.getDataValues()
        );

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(notification.getEmailReceiver());
            helper.setSubject(template.getTemplateSubject());
            helper.setText(templateContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new FailedMailSenderOperation(e.getMessage());
        }
    }

}
