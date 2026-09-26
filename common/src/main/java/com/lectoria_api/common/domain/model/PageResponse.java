package com.lectoria_api.common.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class PageResponse<T> {

    private List<T> data;
    private MetaData metaData;

}
