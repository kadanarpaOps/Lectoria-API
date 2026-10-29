package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.model.BasicInfoModel;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.infrastructure.input.rest.mapper.StoryRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagRestController {
    private final StoryUseCases stories;
    private final StoryRestMapper mapper;
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam(name = "tagName", required = false) String tagName,
                                      @RequestParam(name = "name", required = false) String name,
                                      @RequestParam(defaultValue = "20") int limit,
                                      @RequestParam(defaultValue = "0") int offset) {
        PageResponse<BasicInfoModel> page = stories.searchTags(tagName == null ? name : tagName, offset, limit);
        return mapper.toPageResponse(page);
    }
}
