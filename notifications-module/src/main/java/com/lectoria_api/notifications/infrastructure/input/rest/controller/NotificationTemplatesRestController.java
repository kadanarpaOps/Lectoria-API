package com.lectoria_api.notifications.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.notifications.domain.filters.TemplateFiltersModel;
import com.lectoria_api.notifications.domain.ports.input.TemplateUseCases;
import com.lectoria_api.notifications.infrastructure.input.dto.TemplateGetResponseDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.TemplatePatchRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.dto.TemplatePostRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.mapper.TemplateInputMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class NotificationTemplatesRestController {

    private final TemplateUseCases templateService;
    private final TemplateInputMapper mapper;

    @PostMapping
    public ResponseEntity<Void> createTemplate(@RequestBody TemplatePostRequestDTO templateDTO) {
        templateService.registerTemplate(mapper.toModel(templateDTO));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{templateName}")
    public ResponseEntity<Void> modifyTemplate(@RequestBody TemplatePatchRequestDTO templateDTO, @PathVariable String templateName) {
        templateService.updateTemplate(mapper.toModel(templateDTO), templateName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<TemplateGetResponseDTO>> getPageTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String templateSubject
    ) {
        TemplateFiltersModel filters = TemplateFiltersModel.builder()
                .templateName(templateName)
                .templateSubject(templateSubject)
                .build();
        PaginationRequest paginationRequest = PaginationRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .build();
        return ResponseEntity.ok(
                mapper.toDTO(templateService.getPageTemplates(paginationRequest, filters)));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String templateId) {
        templateService.deleteTemplateById(templateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
