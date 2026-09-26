package com.lectoria_api.common.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaginationRequest {

    private int pageNumber;
    private int pageSize;

}
