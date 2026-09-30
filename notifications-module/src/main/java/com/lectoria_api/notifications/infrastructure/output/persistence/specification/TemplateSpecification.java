package com.lectoria_api.notifications.infrastructure.output.persistence.specification;

import com.lectoria_api.notifications.domain.filters.TemplateFiltersModel;
import com.lectoria_api.notifications.infrastructure.output.persistence.entities.EmailTemplateEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TemplateSpecification {

    private TemplateSpecification () {
    }

    public static Specification<EmailTemplateEntity> withFilters(TemplateFiltersModel filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String templateName = filters.getTemplateName();
            String templateSubject = filters.getTemplateSubject();

            if (validateNotBlank(templateName)) {
                predicates.add(cb.like(cb.lower(root.get("templateName")), "%" + templateName.toLowerCase() + "%"));
            }

            if (validateNotBlank(templateSubject)) {
                predicates.add(cb.like(cb.lower(root.get("templateSubject")), "%" + templateSubject.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean validateNotBlank(String value) {
        return (value != null && !value.isEmpty());
    }

}
