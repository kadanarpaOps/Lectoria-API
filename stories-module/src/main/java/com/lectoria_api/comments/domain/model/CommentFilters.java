package com.lectoria_api.comments.domain.model;

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
public class CommentFilters {
    private UUID storyId;
    private UUID chapterId;
    private Integer offset;
    private Integer limit;
    private Integer pageNumber;
    private Integer pageSize;
}
