package com.lectoria_api.notifications.infrastructure.input.mapper;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.notifications.domain.model.EmailTemplateModel;
import com.lectoria_api.notifications.infrastructure.input.dto.TemplateGetResponseDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.TemplatePatchRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.TemplatePostRequestDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TemplateInputMapper {

    public EmailTemplateModel toModel(TemplatePostRequestDTO dto) {
        if (dto == null) return null;
        return EmailTemplateModel.builder()
                .templateName(dto.getTemplateName())
                .templateSubject(dto.getTemplateSubject())
                .templateLocation(dto.getTemplateLocation())
                .build();
    }

    public EmailTemplateModel toModel(TemplatePatchRequestDTO dto) {
        if (dto == null) return null;
        return EmailTemplateModel.builder()
                .templateSubject(dto.getTemplateSubject())
                .templateLocation(dto.getTemplateLocation())
                .build();
    }

    public TemplateGetResponseDTO toResponseDto(EmailTemplateModel model) {
        if (model == null) return null;
        return TemplateGetResponseDTO.builder()
                .notificationTemplateId(model.getNotificationTemplateId())
                .templateName(model.getTemplateName())
                .templateSubject(model.getTemplateSubject())
                .templateLocation(model.getTemplateLocation())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    public PageResponse<TemplateGetResponseDTO> toDTO(PageResponse<EmailTemplateModel> model) {
        if (model == null) return null;
        List<TemplateGetResponseDTO> data = model.getData() != null
                ? model.getData().stream().map(this::toResponseDto).toList()
                : Collections.emptyList();
        return PageResponse.<TemplateGetResponseDTO>builder()
                .data(data)
                .metaData(model.getMetaData())
                .build();
    }

}
