package com.lectoria_api.stories.domain.model;

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
public class FavoriteModel {
    private UUID id;
    private UUID storyId;
    private UUID userId;
    private LocalDateTime createdAt;
}
