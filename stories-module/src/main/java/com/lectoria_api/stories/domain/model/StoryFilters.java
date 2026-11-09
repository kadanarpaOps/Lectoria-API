package com.lectoria_api.stories.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryFilters {
    private Integer offset;
    private Integer limit;
    private Integer pageNumber;
    private Integer pageSize;
    private String title;
    private String description;
    private String genreName;
    private String secondaryGenreName;
    private UUID genreId;
    private String status;
    private Boolean hidden;
    private Boolean newestFirst;
    private List<String> tagNames;
    private UUID ownerId;
}
