package com.lectoria_api.notifications.application.factory;

import com.lectoria_api.notifications.application.strategies.Notifier;
import com.lectoria_api.notifications.domain.exceptions.business.NotFoundNotifierException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NotifierFactory {

    private final Map<String, Notifier> notifiers;

    public Notifier getNotifier(String notifierName) {
        Notifier notifier = notifiers.get(notifierName);
        if (Objects.isNull(notifier)) {
            throw new NotFoundNotifierException(notifierName);
        }
        return notifier;
    }

}
