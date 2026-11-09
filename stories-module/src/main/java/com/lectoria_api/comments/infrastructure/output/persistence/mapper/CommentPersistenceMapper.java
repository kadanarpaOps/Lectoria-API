package com.lectoria_api.comments.infrastructure.output.persistence.mapper;

import com.lectoria_api.comments.domain.model.CommentModel;
import com.lectoria_api.comments.infrastructure.output.persistence.entities.CommentEntity;
import com.lectoria_api.comments.infrastructure.output.persistence.entities.CommentLikeEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentPersistenceMapper {
    public CommentModel toModel(CommentEntity entity) {
        if (entity == null) return null;
        return CommentModel.builder().id(entity.getId()).username(entity.getUsername()).userId(entity.getUserId())
                .content(entity.getContent()).likes(entity.getLikes()).storyId(entity.getStoryId())
                .chapterId(entity.getChapterId()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }

    public CommentEntity toEntity(CommentModel model) {
        if (model == null) return null;
        return CommentEntity.builder().id(model.getId()).username(model.getUsername()).userId(model.getUserId())
                .content(model.getContent()).likes(model.getLikes()).storyId(model.getStoryId())
                .chapterId(model.getChapterId()).createdAt(model.getCreatedAt()).updatedAt(model.getUpdatedAt()).build();
    }

    public CommentLikeEntity toLikeEntity(java.util.UUID commentId, java.util.UUID userId) {
        return CommentLikeEntity.builder().id(java.util.UUID.randomUUID()).commentId(commentId).userId(userId).build();
    }
}
