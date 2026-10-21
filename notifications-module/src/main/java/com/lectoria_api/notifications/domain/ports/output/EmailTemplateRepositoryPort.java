package com.lectoria_api.notifications.domain.ports.output;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.notifications.domain.filters.TemplateFiltersModel;
import com.lectoria_api.notifications.domain.model.EmailTemplateModel;

import java.util.Optional;

public interface EmailTemplateRepositoryPort {

    void saveTemplate(EmailTemplateModel template);

    PaginationResult<EmailTemplateModel> findTemplates(PaginationRequest paginationRequest, TemplateFiltersModel filters);

    Optional<EmailTemplateModel> findTemplateByName(String templateName);

    Optional<EmailTemplateModel> findTemplateById(String templateId);

    void deleteTemplateById(String templateId);

}
