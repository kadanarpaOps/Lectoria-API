package com.lectoria_api.stories.infrastructure.output.persistence.mapper;

import com.lectoria_api.stories.domain.model.BasicInfoModel;
import com.lectoria_api.stories.domain.model.FavoriteModel;
import com.lectoria_api.stories.domain.model.RatingModel;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.model.ViewModel;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.FavoriteEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.GenreEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.RatingEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.StoryEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.TagEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.ViewEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoryPersistenceMapper {
    public StoryModel toModel(StoryEntity entity) {
        if (entity == null) return null;
        return StoryModel.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .coverUrl(entity.getCoverUrl())
                .coverPublicId(entity.getCoverPublicId())
                .description(entity.getDescription())
                .totalRating(entity.getTotalRating())
                .ratingSum(entity.getRatingSum())
                .ratingCount(entity.getRatingCount())
                .totalChapters(entity.getTotalChapters())
                .hidden(entity.getHidden())
                .status(entity.getStatus())
                .totalViews(entity.getTotalViews())
                .userId(entity.getUserId())
                .totalFavorites(entity.getTotalFavorites())
                .totalComments(entity.getTotalComments())
                .genre(toBasicInfo(entity.getGenre()))
                .secondaryGenre(toBasicInfo(entity.getSecondaryGenre()))
                .tags(entity.getTags().stream().map(this::toBasicInfo).toList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastActivityAt(entity.getLastActivityAt())
                .build();
    }

    public StoryEntity toEntity(StoryModel model, GenreEntity genre, GenreEntity secondaryGenre, List<TagEntity> tags) {
        if (model == null) return null;
        return StoryEntity.builder()
                .id(model.getId())
                .title(model.getTitle())
                .coverUrl(model.getCoverUrl())
                .coverPublicId(model.getCoverPublicId())
                .description(model.getDescription())
                .totalRating(model.getTotalRating())
                .ratingSum(model.getRatingSum())
                .ratingCount(model.getRatingCount())
                .totalChapters(model.getTotalChapters())
                .hidden(model.getHidden())
                .status(model.getStatus())
                .totalViews(model.getTotalViews())
                .userId(model.getUserId())
                .totalFavorites(model.getTotalFavorites())
                .totalComments(model.getTotalComments())
                .genre(genre)
                .secondaryGenre(secondaryGenre)
                .tags(tags)
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .lastActivityAt(model.getLastActivityAt())
                .build();
    }

    public BasicInfoModel toBasicInfo(GenreEntity entity) {
        return entity == null ? null : BasicInfoModel.builder().id(entity.getId()).name(entity.getName()).build();
    }

    public BasicInfoModel toBasicInfo(TagEntity entity) {
        return entity == null ? null : BasicInfoModel.builder().id(entity.getId()).name(entity.getName()).build();
    }

    public RatingModel toModel(RatingEntity entity) {
        if (entity == null) return null;
        return RatingModel.builder().id(entity.getId()).storyId(entity.getStoryId()).userId(entity.getUserId())
                .score(entity.getScore()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }

    public RatingEntity toEntity(RatingModel model) {
        if (model == null) return null;
        return RatingEntity.builder().id(model.getId()).storyId(model.getStoryId()).userId(model.getUserId())
                .score(model.getScore()).createdAt(model.getCreatedAt()).updatedAt(model.getUpdatedAt()).build();
    }

    public FavoriteModel toModel(FavoriteEntity entity) {
        if (entity == null) return null;
        return FavoriteModel.builder().id(entity.getId()).storyId(entity.getStoryId()).userId(entity.getUserId())
                .createdAt(entity.getCreatedAt()).build();
    }

    public FavoriteEntity toEntity(FavoriteModel model) {
        if (model == null) return null;
        return FavoriteEntity.builder().id(model.getId()).storyId(model.getStoryId()).userId(model.getUserId())
                .createdAt(model.getCreatedAt()).build();
    }

    public ViewModel toModel(ViewEntity entity) {
        if (entity == null) return null;
        return ViewModel.builder().id(entity.getId()).storyId(entity.getStoryId()).userId(entity.getUserId())
                .createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }

    public ViewEntity toEntity(ViewModel model) {
        if (model == null) return null;
        return ViewEntity.builder().id(model.getId()).storyId(model.getStoryId()).userId(model.getUserId())
                .createdAt(model.getCreatedAt()).updatedAt(model.getUpdatedAt()).build();
    }
}
