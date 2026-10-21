package com.lectoria_api.notifications.domain.filters;

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
public class TemplateFiltersModel {

    private String templateName;
    private String templateSubject;

}
