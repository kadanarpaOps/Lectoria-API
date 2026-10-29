package com.lectoria_api.stories.infrastructure.output.persistence.specification;

import com.lectoria_api.stories.domain.model.StoryFilters;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.StoryEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class StorySpecification {
    private StorySpecification() {
    }

    public static Specification<StoryEntity> withFilters(StoryFilters filters) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filters.getOwnerId() != null) predicates.add(builder.equal(root.get("userId"), filters.getOwnerId()));
            if (filters.getTitle() != null && !filters.getTitle().isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("title")), pattern(filters.getTitle())));
            }
            if (filters.getDescription() != null && !filters.getDescription().isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("description")), pattern(filters.getDescription())));
            }
            if (filters.getGenreName() != null && !filters.getGenreName().isBlank()) {
                predicates.add(builder.like(builder.lower(root.join("genre").get("name")), pattern(filters.getGenreName())));
            }
            if (filters.getSecondaryGenreName() != null && !filters.getSecondaryGenreName().isBlank()) {
                predicates.add(builder.like(builder.lower(root.join("secondaryGenre").get("name")), pattern(filters.getSecondaryGenreName())));
            }
            if (filters.getGenreId() != null) predicates.add(builder.equal(root.join("genre").get("id"), filters.getGenreId()));
            if (filters.getStatus() != null) predicates.add(builder.equal(root.get("status"), filters.getStatus()));
            if (filters.getHidden() != null) predicates.add(builder.equal(root.get("hidden"), filters.getHidden()));
            if (filters.getTagNames() != null) {
                for (String tagName : filters.getTagNames()) {
                    if (tagName == null || tagName.isBlank()) continue;
                    Join<Object, Object> tagJoin = root.join("tags");
                    predicates.add(builder.equal(builder.lower(tagJoin.get("name")), tagName.trim().toLowerCase()));
                }
            }
            query.distinct(true);
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String pattern(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
