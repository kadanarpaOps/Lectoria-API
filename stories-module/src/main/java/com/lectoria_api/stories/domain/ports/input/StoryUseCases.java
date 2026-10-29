package com.lectoria_api.stories.domain.ports.input;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.model.BasicInfoModel;
import com.lectoria_api.stories.domain.model.FavoriteModel;
import com.lectoria_api.stories.domain.model.RatingModel;
import com.lectoria_api.stories.domain.model.StoryFilters;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.model.ViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface StoryUseCases {
    StoryModel createStory(StoryModel input);
    StoryModel story(UUID id);
    StoryModel storyByTitle(String title);
    StoryModel updateStory(UUID id, StoryModel input);
    StoryModel deleteStory(UUID id);
    StoryModel toggleHidden(UUID id);
    void ensureOwnedStory(UUID id);
    boolean isOwnedStory(UUID id);
    PageResponse<StoryModel> search(StoryFilters filters);
    PageResponse<StoryModel> searchOwnStories(StoryFilters filters);
    StoryModel lastModified();
    List<BasicInfoModel> genres();
    BasicInfoModel genreById(UUID id);
    BasicInfoModel genreByName(String name);
    void updateTags(UUID id, List<String> tags);
    PageResponse<BasicInfoModel> searchTags(String name, int pageNumber, int pageSize);
    RatingModel rate(UUID storyId, BigDecimal score);
    RatingModel rating(UUID storyId);
    FavoriteModel favorite(UUID storyId, boolean add);
    boolean favoriteExists(UUID storyId);
    PageResponse<StoryModel> favorites(int pageNumber, int pageSize);
    ViewModel createView(UUID storyId);
    StoryModel setCover(UUID id, String url, String publicId);
    StoryModel removeCover(UUID id);
    void updateChapterCount(UUID storyId, int count);
    void updateCommentCount(UUID storyId, int count);
}
