package com.lectoria_api.stories.domain.ports.output;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.stories.domain.model.BasicInfoModel;
import com.lectoria_api.stories.domain.model.FavoriteModel;
import com.lectoria_api.stories.domain.model.RatingModel;
import com.lectoria_api.stories.domain.model.StoryFilters;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.model.ViewModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoryRepositoryPort {
    StoryModel save(StoryModel story);
    Optional<StoryModel> findById(UUID id);
    Optional<StoryModel> findByTitle(String title);
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, UUID id);
    void delete(UUID id);
    PaginationResult<StoryModel> search(StoryFilters filters, PaginationRequest request);
    Optional<StoryModel> lastModified(UUID userId);
    List<BasicInfoModel> genres();
    Optional<BasicInfoModel> genreById(UUID id);
    Optional<BasicInfoModel> genreByName(String name);
    Optional<BasicInfoModel> tagByName(String name);
    BasicInfoModel saveTag(BasicInfoModel tag);
    PaginationResult<BasicInfoModel> searchTags(String name, PaginationRequest request);
    Optional<RatingModel> rating(UUID storyId, UUID userId);
    RatingModel saveRating(RatingModel rating);
    long ratingCount(UUID storyId);
    java.math.BigDecimal ratingSum(UUID storyId);
    java.math.BigDecimal averageRating(UUID storyId);
    boolean favoriteExists(UUID storyId, UUID userId);
    FavoriteModel saveFavorite(FavoriteModel favorite);
    void deleteFavorite(UUID storyId, UUID userId);
    long countFavorites(UUID storyId);
    PaginationResult<StoryModel> favorites(UUID userId, PaginationRequest request);
    Optional<ViewModel> view(UUID storyId, UUID userId);
    ViewModel saveView(ViewModel view);
    long countViews(UUID storyId);
}
