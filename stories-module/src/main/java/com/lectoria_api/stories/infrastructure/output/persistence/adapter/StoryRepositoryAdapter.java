package com.lectoria_api.stories.infrastructure.output.persistence.adapter;

import com.lectoria_api.common.domain.model.MetaData;
import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.ports.output.StoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoryRepositoryAdapter implements StoryRepositoryPort {
    private final JdbcTemplate jdbc;

    @Override
    public Map<String, Object> create(Map<String, Object> input) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO T_STORIES (STORY_ID, STORY_TITLE, STORY_DESCRIPTION, USER_ID, GENRE_ID, SECONDARY_GENRE_ID,
                    STORY_STATUS, STORY_HIDDEN)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, id, required(input, "title"), required(input, "description"), uuid(required(input, "userId")),
                uuid(required(input, "genreId")), nullableUuid(input.get("secondaryGenreId")),
                input.getOrDefault("status", "IN_PROGRESS"), input.getOrDefault("hidden", true));
        if (input.get("tags") instanceof List<?> tags) updateTags(id.toString(), tags.stream().map(String::valueOf).toList());
        return findById(id.toString());
    }

    @Override public Map<String, Object> findById(String id) { return one(storyQuery() + " WHERE s.STORY_ID = ?", uuid(id)); }
    @Override public Map<String, Object> findByTitle(String title) { return one(storyQuery() + " WHERE LOWER(s.STORY_TITLE) = LOWER(?)", title); }

    @Override
    public Map<String, Object> update(String id, Map<String, Object> input) {
        if (input.containsKey("title")) jdbc.update("UPDATE T_STORIES SET STORY_TITLE=?, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", input.get("title"), uuid(id));
        if (input.containsKey("description")) jdbc.update("UPDATE T_STORIES SET STORY_DESCRIPTION=?, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", input.get("description"), uuid(id));
        if (input.containsKey("genreId")) jdbc.update("UPDATE T_STORIES SET GENRE_ID=?, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", uuid(input.get("genreId")), uuid(id));
        if (input.containsKey("secondaryGenreId")) jdbc.update("UPDATE T_STORIES SET SECONDARY_GENRE_ID=?, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", nullableUuid(input.get("secondaryGenreId")), uuid(id));
        if (input.containsKey("status")) jdbc.update("UPDATE T_STORIES SET STORY_STATUS=?, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", input.get("status"), uuid(id));
        if (input.containsKey("hidden")) jdbc.update("UPDATE T_STORIES SET STORY_HIDDEN=?, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", input.get("hidden"), uuid(id));
        if (input.get("tags") instanceof List<?> tags) updateTags(id, tags.stream().map(String::valueOf).toList());
        return findById(id);
    }

    @Override
    public Map<String, Object> delete(String id) {
        Map<String, Object> current = findById(id);
        jdbc.update("DELETE FROM T_STORIES WHERE STORY_ID=?", uuid(id));
        return current;
    }

    @Override public Map<String, Object> toggleHidden(String id) {
        jdbc.update("UPDATE T_STORIES SET STORY_HIDDEN = NOT STORY_HIDDEN, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", uuid(id));
        return findById(id);
    }

    @Override
    public PageResponse<Map<String, Object>> search(Map<String, String> filters, String ownerId) {
        int size = Math.max(1, Math.min(50, integer(filters.getOrDefault("limit", filters.get("pageSize")), 20)));
        int page = Math.max(0, integer(filters.get("pageNumber"), integer(filters.get("offset"), 1) - 1));
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> args = new ArrayList<>();
        if (ownerId != null) { where.append(" AND s.USER_ID=?"); args.add(uuid(ownerId)); }
        else if (filters.containsKey("userId")) { where.append(" AND s.USER_ID=?"); args.add(uuid(filters.get("userId"))); }
        addLike(where, args, "s.STORY_TITLE", filters.get("title"));
        addLike(where, args, "s.STORY_DESCRIPTION", filters.get("description"));
        addLike(where, args, "g.GENRE_NAME", filters.get("genreName"));
        if (filters.containsKey("genreId")) { where.append(" AND s.GENRE_ID=?"); args.add(uuid(filters.get("genreId"))); }
        if (filters.containsKey("secondaryGenreName")) {
            where.append(" AND EXISTS (SELECT 1 FROM T_GENRES sg WHERE sg.GENRE_ID=s.SECONDARY_GENRE_ID AND sg.GENRE_NAME ILIKE ?)");
            args.add("%" + filters.get("secondaryGenreName") + "%");
        }
        if (filters.containsKey("status")) { where.append(" AND s.STORY_STATUS=?"); args.add(filters.get("status")); }
        if (filters.containsKey("hidden")) { where.append(" AND s.STORY_HIDDEN=?"); args.add(Boolean.parseBoolean(filters.get("hidden"))); }
        if (filters.containsKey("tagNames")) {
            for (String tag : filters.get("tagNames").split(",")) {
                where.append(" AND EXISTS (SELECT 1 FROM T_STORY_TAGS st JOIN T_TAGS t ON t.TAG_ID=st.TAG_ID WHERE st.STORY_ID=s.STORY_ID AND t.TAG_NAME=?)");
                args.add(tag.trim());
            }
        }
        long count = jdbc.queryForObject("SELECT COUNT(*) FROM T_STORIES s JOIN T_GENRES g ON g.GENRE_ID=s.GENRE_ID" + where, Long.class, args.toArray());
        args.add(size); args.add(page * size);
        String sort = "true".equalsIgnoreCase(filters.get("newestFirst"))
                ? "s.STORY_CREATED_AT DESC" : "s.STORY_LAST_ACTIVITY_AT DESC NULLS LAST, s.STORY_CREATED_AT DESC";
        List<Map<String, Object>> data = jdbc.queryForList(storyQuery() + where + " ORDER BY " + sort + " LIMIT ? OFFSET ?", args.toArray());
        return page(data, page, size, count);
    }

    @Override public Map<String, Object> lastModified(String userId) {
        List<Map<String, Object>> rows = jdbc.queryForList(storyQuery() + " WHERE s.USER_ID=? ORDER BY s.STORY_LAST_ACTIVITY_AT DESC NULLS LAST LIMIT 1", uuid(userId));
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    @Override public List<Map<String, Object>> genres() { return jdbc.queryForList("SELECT GENRE_ID AS \"genreId\", GENRE_NAME AS name FROM T_GENRES ORDER BY GENRE_NAME"); }
    @Override public Map<String, Object> genreById(String id) { return one("SELECT GENRE_ID AS \"genreId\", GENRE_NAME AS name FROM T_GENRES WHERE GENRE_ID=?", uuid(id)); }
    @Override public Map<String, Object> genreByName(String name) { return one("SELECT GENRE_ID AS \"genreId\", GENRE_NAME AS name FROM T_GENRES WHERE LOWER(GENRE_NAME)=LOWER(?)", name); }

    @Override
    @Transactional
    public void updateTags(String storyId, List<String> tagNames) {
        findById(storyId);
        jdbc.update("DELETE FROM T_STORY_TAGS WHERE STORY_ID=?", uuid(storyId));
        for (String name : tagNames) {
            String tag = name == null ? "" : name.trim();
            if (tag.isEmpty()) continue;
            jdbc.update("INSERT INTO T_TAGS(TAG_ID,TAG_NAME) VALUES(?,?) ON CONFLICT (TAG_NAME) DO NOTHING", UUID.randomUUID(), tag);
            jdbc.update("INSERT INTO T_STORY_TAGS(STORY_ID,TAG_ID) SELECT ?,TAG_ID FROM T_TAGS WHERE TAG_NAME=?", uuid(storyId), tag);
        }
    }

    @Override
    public PageResponse<Map<String, Object>> searchTags(String name, int limit, int offset) {
        int size = Math.max(1, Math.min(100, limit));
        String pattern = "%" + (name == null ? "" : name) + "%";
        long count = jdbc.queryForObject("SELECT COUNT(*) FROM T_TAGS WHERE TAG_NAME ILIKE ?", Long.class, pattern);
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT TAG_ID AS \"tagId\", TAG_NAME AS name FROM T_TAGS WHERE TAG_NAME ILIKE ? ORDER BY TAG_NAME LIMIT ? OFFSET ?", pattern, size, Math.max(offset, 0));
        return page(rows, size == 0 ? 0 : Math.max(offset, 0) / size, size, count);
    }

    @Override public Map<String, Object> rate(Map<String, Object> input) {
        UUID story = uuid(required(input, "storyId")), user = uuid(required(input, "userId"));
        double score = Double.parseDouble(String.valueOf(required(input, "score")));
        if (score < 0 || score > 10) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 10");
        jdbc.update("""
                INSERT INTO T_RATINGS(RATING_ID, STORY_ID, USER_ID, RATING_SCORE)
                VALUES(?,?,?,?) ON CONFLICT(STORY_ID,USER_ID) DO UPDATE SET
                RATING_SCORE=EXCLUDED.RATING_SCORE, RATING_UPDATED_AT=CURRENT_TIMESTAMP
                """, UUID.randomUUID(), story, user, score);
        jdbc.update("""
                UPDATE T_STORIES SET STORY_RATING_SUM=(SELECT COALESCE(SUM(RATING_SCORE),0) FROM T_RATINGS WHERE STORY_ID=?),
                STORY_RATING_COUNT=(SELECT COUNT(*) FROM T_RATINGS WHERE STORY_ID=?),
                STORY_TOTAL_RATING=COALESCE((SELECT AVG(RATING_SCORE) FROM T_RATINGS WHERE STORY_ID=?),0)
                WHERE STORY_ID=?
                """, story, story, story, story);
        return rating(story.toString(), user.toString());
    }
    @Override public Map<String, Object> rating(String storyId, String userId) { return one("SELECT RATING_ID AS id, STORY_ID AS \"storyId\", USER_ID AS \"userId\", RATING_SCORE AS score FROM T_RATINGS WHERE STORY_ID=? AND USER_ID=?", uuid(storyId), uuid(userId)); }

    @Override public Map<String, Object> favorite(String storyId, String userId, boolean add) {
        UUID story = uuid(storyId), user = uuid(userId);
        if (add) jdbc.update("INSERT INTO T_FAVORITE_STORIES(FAVORITE_STORY_ID,STORY_ID,USER_ID) VALUES(?,?,?) ON CONFLICT(STORY_ID,USER_ID) DO NOTHING", UUID.randomUUID(), story, user);
        else jdbc.update("DELETE FROM T_FAVORITE_STORIES WHERE STORY_ID=? AND USER_ID=?", story, user);
        jdbc.update("UPDATE T_STORIES SET STORY_TOTAL_FAVORITES=(SELECT COUNT(*) FROM T_FAVORITE_STORIES WHERE STORY_ID=?) WHERE STORY_ID=?", story, story);
        return Map.of("storyId", storyId, "userId", userId, "favorite", add);
    }
    @Override public boolean favoriteExists(String storyId, String userId) { return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM T_FAVORITE_STORIES WHERE STORY_ID=? AND USER_ID=?)", Boolean.class, uuid(storyId), uuid(userId))); }
    @Override public PageResponse<Map<String, Object>> favorites(String userId, Map<String, String> filters) {
        int page = integer(filters.get("pageNumber"), 0), size = Math.max(1, Math.min(100, integer(filters.get("pageSize"), 10)));
        UUID user = uuid(userId);
        long count = jdbc.queryForObject("SELECT COUNT(*) FROM T_FAVORITE_STORIES WHERE USER_ID=?", Long.class, user);
        List<Map<String, Object>> data = jdbc.queryForList(storyQuery() + " JOIN T_FAVORITE_STORIES f ON f.STORY_ID=s.STORY_ID WHERE f.USER_ID=? ORDER BY f.FAVORITE_CREATED_AT DESC LIMIT ? OFFSET ?", user, size, page * size);
        return page(data, page, size, count);
    }

    @Override public Map<String, Object> createView(String storyId, String userId) {
        UUID story = uuid(storyId), user = uuid(userId);
        jdbc.update("INSERT INTO T_VIEWS(VIEW_ID,STORY_ID,USER_ID) VALUES(?,?,?) ON CONFLICT(STORY_ID,USER_ID) DO UPDATE SET VIEW_UPDATED_AT=CURRENT_TIMESTAMP", UUID.randomUUID(), story, user);
        jdbc.update("UPDATE T_STORIES SET STORY_TOTAL_VIEWS=(SELECT COUNT(*) FROM T_VIEWS WHERE STORY_ID=?) WHERE STORY_ID=?", story, story);
        return Map.of("storyId", storyId, "userId", userId);
    }
    @Override public Map<String, Object> setCover(String id, String url, String publicId) {
        jdbc.update("UPDATE T_STORIES SET STORY_COVER_URL=?, STORY_COVER_PUBLIC_ID=?, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", url, publicId, uuid(id));
        return findById(id);
    }
    @Override public Map<String, Object> removeCover(String id) {
        jdbc.update("UPDATE T_STORIES SET STORY_COVER_URL='', STORY_COVER_PUBLIC_ID=NULL, STORY_UPDATED_AT=CURRENT_TIMESTAMP WHERE STORY_ID=?", uuid(id));
        return findById(id);
    }

    private String storyQuery() {
        return """
                SELECT s.STORY_ID AS id, s.STORY_TITLE AS title, s.STORY_COVER_URL AS "coverUrl",
                s.STORY_COVER_PUBLIC_ID AS "coverPublicId", s.STORY_DESCRIPTION AS description,
                s.STORY_TOTAL_RATING AS "totalRating", s.STORY_RATING_COUNT AS "ratingCount",
                s.STORY_TOTAL_CHAPTERS AS "totalChapters", s.STORY_HIDDEN AS hidden, s.STORY_STATUS AS status,
                s.STORY_TOTAL_VIEWS AS "totalViews", s.USER_ID AS "userId", s.USER_ID AS author,
                s.STORY_TOTAL_FAVORITES AS "totalFavorites", s.STORY_TOTAL_COMMENTS AS "totalComments",
                s.GENRE_ID AS "genreId", g.GENRE_NAME AS genre, s.SECONDARY_GENRE_ID AS "secondaryGenreId",
                s.STORY_CREATED_AT AS "createdAt", s.STORY_UPDATED_AT AS "updatedAt",
                s.STORY_LAST_ACTIVITY_AT AS "lastActivityAt",
                COALESCE((SELECT ARRAY_AGG(t.TAG_NAME ORDER BY t.TAG_NAME) FROM T_STORY_TAGS st
                    JOIN T_TAGS t ON t.TAG_ID=st.TAG_ID WHERE st.STORY_ID=s.STORY_ID), ARRAY[]::VARCHAR[]) AS tags
                FROM T_STORIES s JOIN T_GENRES g ON g.GENRE_ID=s.GENRE_ID
                """;
    }

    private Map<String, Object> one(String sql, Object... args) {
        List<Map<String, Object>> rows = jdbc.queryForList(sql, args);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        return rows.get(0);
    }
    private PageResponse<Map<String, Object>> page(List<Map<String, Object>> data, int number, int size, long total) {
        return PageResponse.<Map<String, Object>>builder().data(data).metaData(MetaData.builder()
                .numberPage(number).pageSize(size).totalItems(Math.toIntExact(total))
                .totalPages((int) Math.ceil((double) total / size)).build()).build();
    }
    private void addLike(StringBuilder where, List<Object> args, String column, String value) {
        if (value != null && !value.isBlank()) { where.append(" AND ").append(column).append(" ILIKE ?"); args.add("%" + value + "%"); }
    }
    private static int integer(String value, int fallback) { try { return value == null ? fallback : Integer.parseInt(value); } catch (NumberFormatException ignored) { return fallback; } }
    private static Object required(Map<String, Object> input, String key) {
        Object value = input.get(key);
        if (value == null || String.valueOf(value).isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " is required");
        return value;
    }
    private static UUID uuid(Object value) {
        try { return value instanceof UUID id ? id : UUID.fromString(String.valueOf(value)); }
        catch (RuntimeException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid identifier"); }
    }
    private static UUID nullableUuid(Object value) { return value == null || String.valueOf(value).isBlank() ? null : uuid(value); }
}
