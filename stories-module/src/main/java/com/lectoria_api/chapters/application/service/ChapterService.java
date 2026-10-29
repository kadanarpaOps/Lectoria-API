package com.lectoria_api.chapters.application.service;

import com.lectoria_api.chapters.domain.ports.input.ChapterUseCases;
import com.lectoria_api.chapters.domain.ports.output.ChapterRepositoryPort;
import com.lectoria_api.common.domain.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChapterService implements ChapterUseCases {
    private final ChapterRepositoryPort chapters;

    @Override public Map<String, Object> createChapter(Map<String, Object> input) { return chapters.createChapter(input); }
    @Override public Map<String, Object> chapter(String id) { return chapters.chapter(id); }
    @Override public PageResponse<Map<String, Object>> chapters(String storyId, Map<String, String> query, boolean owned) {
        return chapters.chapters(storyId, query, owned);
    }
    @Override public Map<String, Object> updateChapter(String id, Map<String, Object> input) { return chapters.updateChapter(id, input); }
    @Override public Map<String, Object> toggleChapter(String id) { return chapters.toggleChapter(id); }
    @Override public void deleteChapter(String storyId, String chapterId) { chapters.deleteChapter(storyId, chapterId); }
}
