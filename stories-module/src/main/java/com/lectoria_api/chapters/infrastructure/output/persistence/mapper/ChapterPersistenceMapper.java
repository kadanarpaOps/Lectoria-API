package com.lectoria_api.chapters.infrastructure.output.persistence.mapper;

import com.lectoria_api.chapters.domain.model.ChapterModel;
import com.lectoria_api.chapters.infrastructure.output.persistence.entities.ChapterEntity;
import org.springframework.stereotype.Component;

@Component
public class ChapterPersistenceMapper {
    public ChapterModel toModel(ChapterEntity entity) {
        if (entity == null) return null;
        return ChapterModel.builder().id(entity.getId()).title(entity.getTitle()).order(entity.getOrder())
                .content(entity.getContent()).hidden(entity.getHidden()).totalComments(entity.getTotalComments())
                .storyId(entity.getStoryId()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())
                .lastActivityAt(entity.getLastActivityAt()).build();
    }

    public ChapterEntity toEntity(ChapterModel model) {
        if (model == null) return null;
        return ChapterEntity.builder().id(model.getId()).title(model.getTitle()).order(model.getOrder())
                .content(model.getContent()).hidden(model.getHidden()).totalComments(model.getTotalComments())
                .storyId(model.getStoryId()).createdAt(model.getCreatedAt()).updatedAt(model.getUpdatedAt())
                .lastActivityAt(model.getLastActivityAt()).build();
    }
}
