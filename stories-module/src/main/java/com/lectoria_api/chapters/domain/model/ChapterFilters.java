package com.lectoria_api.chapters.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterFilters {
    private UUID storyId;
    private Integer offset;
    private Integer limit;
    private Integer pageNumber;
    private Integer pageSize;
    private Boolean visibleOnly;
    private Boolean newestFirst;
}
