package com.lectoria_api.notifications.infrastructure.output.persistence.adapter;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.notifications.domain.filters.TemplateFiltersModel;
import com.lectoria_api.notifications.domain.model.EmailTemplateModel;
import com.lectoria_api.notifications.domain.ports.output.EmailTemplateRepositoryPort;
import com.lectoria_api.notifications.infrastructure.output.persistence.entities.EmailTemplateEntity;
import com.lectoria_api.notifications.infrastructure.output.persistence.mapper.NotificationPersistenceMapper;
import com.lectoria_api.notifications.infrastructure.output.persistence.repository.JpaEmailTemplateRepository;
import com.lectoria_api.notifications.infrastructure.output.persistence.specification.TemplateSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailTemplateRepositoryAdapter implements EmailTemplateRepositoryPort {

    private final JpaEmailTemplateRepository templateRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public void saveTemplate(EmailTemplateModel template) {
        templateRepository.save(mapper.toEntity(template));
    }

    @Override
    public PaginationResult<EmailTemplateModel> findTemplates(PaginationRequest paginationRequest, TemplateFiltersModel filters) {
        Pageable pageable = PageRequest.of(
                paginationRequest.getPageNumber(),
                paginationRequest.getPageSize()
        );

        Page<EmailTemplateEntity> pageResult = templateRepository.findAll(TemplateSpecification.withFilters(filters), pageable);
        List<EmailTemplateModel> templates = mapper.toModel(pageResult.getContent());

        return PaginationResult.<EmailTemplateModel>builder()
                .content(templates)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Override
    public Optional<EmailTemplateModel> findTemplateByName(String templateName) {
        return templateRepository.findByTemplateName(templateName).map(mapper::toModel);
    }

    @Override
    public Optional<EmailTemplateModel> findTemplateById(String templateId) {
        return templateRepository.findById(templateId).map(mapper::toModel);
    }

    @Override
    public void deleteTemplateById(String templateId) {
        templateRepository.deleteById(templateId);
    }

}
