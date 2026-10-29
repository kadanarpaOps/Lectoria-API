package com.lectoria_api.chapters.domain.ports.input;

import com.lectoria_api.common.domain.model.PageResponse;

import java.util.Map;

public interface ChapterUseCases {
    Map<String, Object> createChapter(Map<String, Object> input);
    Map<String, Object> chapter(String id);
    PageResponse<Map<String, Object>> chapters(String storyId, Map<String, String> query, boolean owned);
    Map<String, Object> updateChapter(String id, Map<String, Object> input);
    Map<String, Object> toggleChapter(String id);
    void deleteChapter(String storyId, String chapterId);
}
