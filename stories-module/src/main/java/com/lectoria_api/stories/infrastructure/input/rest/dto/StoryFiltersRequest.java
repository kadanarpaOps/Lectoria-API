package com.lectoria_api.stories.infrastructure.input.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class StoryFiltersRequest {
    private Integer offset;
    private Integer limit;
    private String title;
    private String description;
    private String genreName;
    private String secondaryGenreName;
    private UUID genreId;
    private String status;
    private Boolean hidden;
    private Boolean newestFirst;
    private List<String> tagNames;
}
