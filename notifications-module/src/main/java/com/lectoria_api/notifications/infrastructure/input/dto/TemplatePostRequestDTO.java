package com.lectoria_api.notifications.infrastructure.input.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplatePostRequestDTO {

    private String templateName;
    private String templateSubject;
    private String templateLocation;

}
