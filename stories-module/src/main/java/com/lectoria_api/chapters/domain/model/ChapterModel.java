package com.lectoria_api.chapters.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterModel {
    private UUID id;
    private String title;
    private Integer order;
    private String content;
    private Boolean hidden;
    private Integer totalComments;
    private UUID storyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;
}
