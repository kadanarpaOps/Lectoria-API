package com.lectoria_api.notifications.domain.ports.input;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.notifications.domain.filters.TemplateFiltersModel;
import com.lectoria_api.notifications.domain.model.EmailTemplateModel;

public interface TemplateUseCases {

    void registerTemplate(EmailTemplateModel template);

    void updateTemplate(EmailTemplateModel template, String templateName);

    PageResponse<EmailTemplateModel> getPageTemplates(PaginationRequest paginationRequest, TemplateFiltersModel filters);

    void deleteTemplateById(String templateId);

}
