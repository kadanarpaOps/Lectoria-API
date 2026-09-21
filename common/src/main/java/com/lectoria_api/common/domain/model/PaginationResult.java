package com.lectoria_api.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class PaginationResult<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public MetaData toMetaData() {
        return MetaData.builder()
                .numberPage(pageNumber)
                .pageSize(pageSize)
                .totalItems((int) totalElements)
                .totalPages(totalPages)
                .build();
    }


}
