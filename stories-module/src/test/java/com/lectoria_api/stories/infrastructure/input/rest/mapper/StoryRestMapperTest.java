package com.lectoria_api.stories.infrastructure.input.rest.mapper;

import com.lectoria_api.stories.domain.model.StoryModel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoryRestMapperTest {

    private final StoryRestMapper mapper = new StoryRestMapper();

    @Test
    void includesAuthorUsernameInStoryResponse() {
        StoryModel story = StoryModel.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username("lectora")
                .build();

        assertEquals("lectora", mapper.toResponse(story).get("username"));
    }
}
