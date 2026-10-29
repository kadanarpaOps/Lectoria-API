package com.lectoria_api.stories.infrastructure.output.persistence.adapter;

import com.lectoria_api.comments.domain.ports.output.CommentRepositoryPort;
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
public class CommentRepositoryAdapter implements CommentRepositoryPort {
    private final JdbcTemplate jdbc;

    @Override public Map<String, Object> createComment(Map<String, Object> input) {
        UUID id = UUID.randomUUID();
        Object story = input.get("storyId"), chapter = input.get("chapterId");
        jdbc.update("""
                INSERT INTO T_COMMENTS(COMMENT_ID,COMMENT_USERNAME,USER_ID,COMMENT_CONTENT,STORY_ID,CHAPTER_ID)
                VALUES(?,?,?,?,?,?)
                """, id, required(input, "username"), String.valueOf(required(input, "userId")),
                required(input, "content"), story == null ? null : uuid(story), chapter == null ? null : uuid(chapter));
        if (story != null) jdbc.update("UPDATE T_STORIES SET STORY_TOTAL_COMMENTS=STORY_TOTAL_COMMENTS+1 WHERE STORY_ID=?", uuid(story));
        if (chapter != null) jdbc.update("UPDATE T_CHAPTERS SET CHAPTER_TOTAL_COMMENTS=CHAPTER_TOTAL_COMMENTS+1 WHERE CHAPTER_ID=?", uuid(chapter));
        return comment(id.toString());
    }
    @Override public Map<String, Object> comment(String id) { return one(select() + " WHERE c.COMMENT_ID=?", uuid(id)); }
    @Override public PageResponse<Map<String, Object>> comments(String parentId, boolean byStory, Map<String, String> query) {
        int page = integer(query.get("pageNumber"), 0), size = Math.max(1, Math.min(100, integer(query.get("pageSize"), 10)));
        String column = byStory ? "c.STORY_ID" : "c.CHAPTER_ID";
        UUID parent = uuid(parentId);
        long total = jdbc.queryForObject("SELECT COUNT(*) FROM T_COMMENTS c WHERE " + column + "=?", Long.class, parent);
        List<Map<String, Object>> rows = jdbc.queryForList(select() + " WHERE " + column + "=? ORDER BY c.COMMENT_CREATED_AT DESC LIMIT ? OFFSET ?", parent, size, page * size);
        return PageResponse.<Map<String, Object>>builder().data(rows).metaData(MetaData.builder().numberPage(page).pageSize(size)
                .totalItems((int) total).totalPages((int) Math.ceil((double) total / size)).build()).build();
    }
    @Override public Map<String, Object> updateComment(String id, Map<String, Object> input) {
        if (!input.containsKey("content")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        jdbc.update("UPDATE T_COMMENTS SET COMMENT_CONTENT=?,COMMENT_UPDATED_AT=CURRENT_TIMESTAMP WHERE COMMENT_ID=?", input.get("content"), uuid(id));
        return comment(id);
    }
    @Override public void deleteComment(String id) {
        Map<String, Object> old = comment(id);
        jdbc.update("DELETE FROM T_COMMENTS WHERE COMMENT_ID=?", uuid(id));
        if (old.get("storyId") != null) jdbc.update("UPDATE T_STORIES SET STORY_TOTAL_COMMENTS=GREATEST(0,STORY_TOTAL_COMMENTS-1) WHERE STORY_ID=?", uuid(old.get("storyId")));
        if (old.get("chapterId") != null) jdbc.update("UPDATE T_CHAPTERS SET CHAPTER_TOTAL_COMMENTS=GREATEST(0,CHAPTER_TOTAL_COMMENTS-1) WHERE CHAPTER_ID=?", uuid(old.get("chapterId")));
    }
    @Override public Map<String, Object> toggleLike(String commentId, String userId) {
        UUID comment = uuid(commentId), user = uuid(userId);
        Integer removed = jdbc.update("DELETE FROM T_COMMENT_LIKES WHERE COMMENT_ID=? AND USER_ID=?", comment, user);
        if (removed == 0) jdbc.update("INSERT INTO T_COMMENT_LIKES(COMMENT_LIKE_ID,COMMENT_ID,USER_ID) VALUES(?,?,?) ON CONFLICT(COMMENT_ID,USER_ID) DO NOTHING", UUID.randomUUID(), comment, user);
        jdbc.update("UPDATE T_COMMENTS SET COMMENT_LIKES=(SELECT COUNT(*) FROM T_COMMENT_LIKES WHERE COMMENT_ID=?) WHERE COMMENT_ID=?", comment, comment);
        return comment(commentId);
    }
    private String select() { return """
            SELECT c.COMMENT_ID AS id,c.COMMENT_USERNAME AS username,c.USER_ID AS "userId",
            c.COMMENT_CONTENT AS content,c.COMMENT_LIKES AS likes,c.STORY_ID AS "storyId",
            c.CHAPTER_ID AS "chapterId",c.COMMENT_CREATED_AT AS "createdAt",c.COMMENT_UPDATED_AT AS "updatedAt"
            FROM T_COMMENTS c
            """; }
    private Map<String, Object> one(String sql, Object... args) {
        List<Map<String, Object>> rows = jdbc.queryForList(sql, args);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        return rows.get(0);
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
