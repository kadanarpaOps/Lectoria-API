package com.lectoria_api.stories.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryModel {
    private UUID id;
    private String title;
    private String coverUrl;
    private String coverPublicId;
    private String description;
    private BigDecimal totalRating;
    private BigDecimal ratingSum;
    private Integer ratingCount;
    private Integer totalChapters;
    private Boolean hidden;
    private String status;
    private Integer totalViews;
    private UUID userId;
    private Integer totalFavorites;
    private Integer totalComments;
    private BasicInfoModel genre;
    private BasicInfoModel secondaryGenre;
    private List<BasicInfoModel> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;
}
