package com.lectoria_api.notifications.infrastructure.output.mail.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateResolver {

    private final SpringTemplateEngine templateEngine;

    public String resolve(String templateLocation, Map<String, String> values) {
        Context context = new Context();
        context.setVariables(new HashMap<>(values));

        return templateEngine.process(templateLocation, context);
    }

}
