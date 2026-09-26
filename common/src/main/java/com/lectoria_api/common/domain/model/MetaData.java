package com.lectoria_api.common.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MetaData {

    private int numberPage;
    private int pageSize;
    private int totalItems;
    private int totalPages;

}
