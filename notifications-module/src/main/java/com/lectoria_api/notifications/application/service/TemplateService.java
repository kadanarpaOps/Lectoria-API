package com.lectoria_api.notifications.application.service;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.notifications.domain.exceptions.business.NotFoundTemplateException;
import com.lectoria_api.notifications.domain.filters.TemplateFiltersModel;
import com.lectoria_api.notifications.domain.model.EmailTemplateModel;
import com.lectoria_api.notifications.domain.ports.input.TemplateUseCases;
import com.lectoria_api.notifications.domain.ports.output.EmailTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateService implements TemplateUseCases {

    private final EmailTemplateRepositoryPort templateRepository;

    @Override
    public void registerTemplate(EmailTemplateModel template) {
        templateRepository.saveTemplate(template);
    }

    @Override
    public void updateTemplate(EmailTemplateModel template, String templateName) {
        EmailTemplateModel toUpdateTemplate = getTemplateByNameIdentifier(templateName);
        mapTemplateValues(toUpdateTemplate, template);
        templateRepository.saveTemplate(toUpdateTemplate);
    }

    @Override
    public PageResponse<EmailTemplateModel> getPageTemplates(PaginationRequest paginationRequest, TemplateFiltersModel filters) {
        PaginationResult<EmailTemplateModel> pageResult = templateRepository.findTemplates(paginationRequest, filters);

        return PageResponse.<EmailTemplateModel>builder()
                .data(pageResult.getContent())
                .metaData(pageResult.toMetaData())
                .build();
    }

    @Override
    public void deleteTemplateById(String templateId) {
        templateRepository.findTemplateById(templateId)
                .orElseThrow(() -> new NotFoundTemplateException(templateId));
        templateRepository.deleteTemplateById(templateId);
    }

    private EmailTemplateModel getTemplateByNameIdentifier(String templateName) {
        return templateRepository.findTemplateByName(templateName)
                .orElseThrow(() -> new NotFoundTemplateException(templateName));
    }

    private void mapTemplateValues(EmailTemplateModel target, EmailTemplateModel source) {
        if (source.getTemplateLocation() != null) target.setTemplateLocation(source.getTemplateLocation());
        if (source.getTemplateSubject() != null) target.setTemplateSubject(source.getTemplateSubject());
    }

}
