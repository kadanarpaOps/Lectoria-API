package com.lectoria_api.stories.infrastructure.output.persistence.adapter;

import com.lectoria_api.chapters.domain.ports.output.ChapterRepositoryPort;
import com.lectoria_api.common.domain.model.MetaData;
import com.lectoria_api.common.domain.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChapterRepositoryAdapter implements ChapterRepositoryPort {
    private final JdbcTemplate jdbc;

    @Override public Map<String, Object> createChapter(Map<String, Object> input) {
        UUID id = UUID.randomUUID();
        UUID storyId = uuid(required(input, "storyId"));
        jdbc.update("""
                INSERT INTO T_CHAPTERS(CHAPTER_ID,CHAPTER_TITLE,CHAPTER_ORDER,CHAPTER_CONTENT,CHAPTER_HIDDEN,STORY_ID)
                VALUES(?,?,?,?,?,?)
                """, id, required(input, "title"), Integer.parseInt(String.valueOf(input.getOrDefault("order", 1))),
                required(input, "content"), input.getOrDefault("hidden", true), storyId);
        jdbc.update("UPDATE T_STORIES SET STORY_TOTAL_CHAPTERS=(SELECT COUNT(*) FROM T_CHAPTERS WHERE STORY_ID=?) WHERE STORY_ID=?", storyId, storyId);
        return chapter(id.toString());
    }

    @Override public Map<String, Object> chapter(String id) { return one(select() + " WHERE c.CHAPTER_ID=?", uuid(id)); }
    @Override public PageResponse<Map<String, Object>> chapters(String storyId, Map<String, String> query, boolean owned) {
        int page = integer(query.get("pageNumber"), 0), size = Math.max(1, Math.min(100, integer(query.get("pageSize"), 10)));
        UUID story = uuid(storyId);
        String filter = " WHERE c.STORY_ID=?" + (owned ? "" : " AND c.CHAPTER_HIDDEN=FALSE");
        long total = jdbc.queryForObject("SELECT COUNT(*) FROM T_CHAPTERS c" + filter, Long.class, story);
        List<Map<String, Object>> rows = jdbc.queryForList(select() + filter + " ORDER BY c.CHAPTER_ORDER LIMIT ? OFFSET ?", story, size, page * size);
        return page(rows, page, size, total);
    }
    @Override public Map<String, Object> updateChapter(String id, Map<String, Object> input) {
        UUID chapter = uuid(id);
        if (input.containsKey("title")) jdbc.update("UPDATE T_CHAPTERS SET CHAPTER_TITLE=?,CHAPTER_UPDATED_AT=CURRENT_TIMESTAMP WHERE CHAPTER_ID=?", input.get("title"), chapter);
        if (input.containsKey("order")) jdbc.update("UPDATE T_CHAPTERS SET CHAPTER_ORDER=?,CHAPTER_UPDATED_AT=CURRENT_TIMESTAMP WHERE CHAPTER_ID=?", input.get("order"), chapter);
        if (input.containsKey("content")) jdbc.update("UPDATE T_CHAPTERS SET CHAPTER_CONTENT=?,CHAPTER_UPDATED_AT=CURRENT_TIMESTAMP WHERE CHAPTER_ID=?", input.get("content"), chapter);
        if (input.containsKey("hidden")) jdbc.update("UPDATE T_CHAPTERS SET CHAPTER_HIDDEN=?,CHAPTER_UPDATED_AT=CURRENT_TIMESTAMP WHERE CHAPTER_ID=?", input.get("hidden"), chapter);
        return chapter(id);
    }
    @Override public Map<String, Object> toggleChapter(String id) {
        jdbc.update("UPDATE T_CHAPTERS SET CHAPTER_HIDDEN=NOT CHAPTER_HIDDEN,CHAPTER_UPDATED_AT=CURRENT_TIMESTAMP WHERE CHAPTER_ID=?", uuid(id));
        return chapter(id);
    }
    @Override public void deleteChapter(String storyId, String chapterId) {
        int deleted = jdbc.update("DELETE FROM T_CHAPTERS WHERE CHAPTER_ID=? AND STORY_ID=?", uuid(chapterId), uuid(storyId));
        if (deleted == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found");
        jdbc.update("UPDATE T_STORIES SET STORY_TOTAL_CHAPTERS=(SELECT COUNT(*) FROM T_CHAPTERS WHERE STORY_ID=?) WHERE STORY_ID=?", uuid(storyId), uuid(storyId));
    }
    private String select() { return """
            SELECT c.CHAPTER_ID AS id,c.CHAPTER_TITLE AS title,c.CHAPTER_ORDER AS "order",
            c.CHAPTER_CONTENT AS content,c.CHAPTER_HIDDEN AS hidden,c.CHAPTER_TOTAL_COMMENTS AS "totalComments",
            c.STORY_ID AS "storyId",c.CHAPTER_CREATED_AT AS "createdAt",c.CHAPTER_UPDATED_AT AS "updatedAt"
            FROM T_CHAPTERS c
            """; }
    private Map<String, Object> one(String sql, Object... args) {
        List<Map<String, Object>> rows = jdbc.queryForList(sql, args);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found");
        return rows.get(0);
    }
    private PageResponse<Map<String, Object>> page(List<Map<String, Object>> rows, int page, int size, long total) {
        return PageResponse.<Map<String, Object>>builder().data(rows).metaData(MetaData.builder().numberPage(page).pageSize(size)
                .totalItems((int) total).totalPages((int) Math.ceil((double) total / size)).build()).build();
    }
    private static int integer(String value, int fallback) { try { return value == null ? fallback : Integer.parseInt(value); } catch (NumberFormatException ignored) { return fallback; } }
    private static Object required(Map<String, Object> input, String key) {
        Object value = input.get(key);
        if (value == null || String.valueOf(value).isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " is required");
        return value;
    }
    private static UUID uuid(Object value) {
        try { return UUID.fromString(String.valueOf(value)); }
        catch (RuntimeException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid identifier"); }
    }
}
