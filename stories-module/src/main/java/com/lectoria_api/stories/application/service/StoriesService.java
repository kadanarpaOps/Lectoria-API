package com.lectoria_api.stories.application.service;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.stories.domain.exceptions.GenreNotFoundException;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.domain.exceptions.StoryNotFoundException;
import com.lectoria_api.stories.domain.exceptions.StoryOwnershipException;
import com.lectoria_api.stories.domain.exceptions.StoryTitleAlreadyExistsException;
import com.lectoria_api.stories.domain.model.BasicInfoModel;
import com.lectoria_api.stories.domain.model.FavoriteModel;
import com.lectoria_api.stories.domain.model.RatingModel;
import com.lectoria_api.stories.domain.model.StoryFilters;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.model.ViewModel;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.domain.ports.output.StoryRepositoryPort;
import com.lectoria_api.users.domain.ports.input.SharedAuthUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoriesService implements StoryUseCases {
    private static final int MAX_PAGE_SIZE = 100;
    private static final List<String> STORY_STATUSES = List.of("IN_PROGRESS", "COMPLETED", "ABANDONED", "PAUSED");
    private final StoryRepositoryPort stories;
    private final SharedAuthUseCases authService;

    @Override
    @Transactional
    public StoryModel createStory(StoryModel input) {
        validateStoryInput(input, true);
        if (stories.existsByTitle(input.getTitle().trim())) throw new StoryTitleAlreadyExistsException();
        UUID userId = authService.getAuthUserId();
        StoryModel story = StoryModel.builder()
                .id(UUID.randomUUID())
                .title(input.getTitle().trim())
                .coverUrl("")
                .description(input.getDescription().trim())
                .totalRating(BigDecimal.ZERO)
                .ratingSum(BigDecimal.ZERO)
                .ratingCount(0)
                .totalChapters(0)
                .hidden(input.getHidden() == null || input.getHidden())
                .status(input.getStatus() == null ? "IN_PROGRESS" : input.getStatus())
                .totalViews(0)
                .userId(userId)
                .totalFavorites(0)
                .totalComments(0)
                .genre(resolveGenre(input.getGenre()))
                .secondaryGenre(resolveOptionalGenre(input.getSecondaryGenre()))
                .tags(resolveTags(input.getTags()))
                .build();
        return stories.save(story);
    }

    @Override
    public StoryModel story(UUID id) {
        StoryModel story = stories.findById(id).orElseThrow(StoryNotFoundException::new);
        if (Boolean.TRUE.equals(story.getHidden()) && !authService.getAuthUserId().equals(story.getUserId())) {
            throw new StoryNotFoundException();
        }
        return story;
    }

    @Override
    public StoryModel storyByTitle(String title) {
        StoryModel story = stories.findByTitle(title).orElseThrow(StoryNotFoundException::new);
        if (Boolean.TRUE.equals(story.getHidden()) && !authService.getAuthUserId().equals(story.getUserId())) {
            throw new StoryNotFoundException();
        }
        return story;
    }

    @Override
    @Transactional
    public StoryModel updateStory(UUID id, StoryModel input) {
        StoryModel story = requireOwnedStory(id);
        validateStoryInput(input, false);
        if (input.getTitle() != null && !story.getTitle().equalsIgnoreCase(input.getTitle().trim())) {
            if (stories.existsByTitleAndIdNot(input.getTitle().trim(), id)) throw new StoryTitleAlreadyExistsException();
            story.setTitle(input.getTitle().trim());
        }
        if (input.getDescription() != null) story.setDescription(input.getDescription().trim());
        if (input.getGenre() != null) story.setGenre(resolveGenre(input.getGenre()));
        if (input.getSecondaryGenre() != null) story.setSecondaryGenre(resolveOptionalGenre(input.getSecondaryGenre()));
        if (input.getStatus() != null) story.setStatus(input.getStatus());
        if (input.getHidden() != null) story.setHidden(input.getHidden());
        if (input.getTags() != null) story.setTags(resolveTags(input.getTags()));
        return stories.save(story);
    }

    @Override
    @Transactional
    public StoryModel deleteStory(UUID id) {
        StoryModel story = requireOwnedStory(id);
        stories.delete(id);
        return story;
    }

    @Override
    @Transactional
    public StoryModel toggleHidden(UUID id) {
        StoryModel story = requireOwnedStory(id);
        story.setHidden(!Boolean.TRUE.equals(story.getHidden()));
        return stories.save(story);
    }

    @Override
    public void ensureOwnedStory(UUID id) {
        requireOwnedStory(id);
    }

    @Override
    public boolean isOwnedStory(UUID id) {
        return authService.getAuthUserId().equals(story(id).getUserId());
    }

    @Override
    public PageResponse<StoryModel> search(StoryFilters filters) {
        if (filters == null) filters = new StoryFilters();
        if (filters.getOwnerId() == null) {
            if (Boolean.TRUE.equals(filters.getHidden())) filters.setOwnerId(authService.getAuthUserId());
            else filters.setHidden(false);
        }
        return response(stories.search(filters, pagination(filters.getPageNumber(), filters.getPageSize(),
                filters.getOffset(), filters.getLimit())));
    }

    @Override
    public PageResponse<StoryModel> searchOwnStories(StoryFilters filters) {
        if (filters == null) filters = new StoryFilters();
        filters.setOwnerId(authService.getAuthUserId());
        return search(filters);
    }

    @Override
    public StoryModel lastModified() {
        return stories.lastModified(authService.getAuthUserId()).orElse(null);
    }

    @Override
    public List<BasicInfoModel> genres() {
        return stories.genres();
    }

    @Override
    public BasicInfoModel genreById(UUID id) {
        return stories.genreById(id).orElseThrow(GenreNotFoundException::new);
    }

    @Override
    public BasicInfoModel genreByName(String name) {
        return stories.genreByName(name).orElseThrow(GenreNotFoundException::new);
    }

    @Override
    @Transactional
    public void updateTags(UUID id, List<String> names) {
        StoryModel story = requireOwnedStory(id);
        story.setTags(resolveTags(names.stream().map(name -> BasicInfoModel.builder().name(name).build()).toList()));
        stories.save(story);
    }

    @Override
    public PageResponse<BasicInfoModel> searchTags(String name, int offset, int limit) {
        PaginationResult<BasicInfoModel> page = stories.searchTags(name == null ? "" : name,
                pagination(null, null, offset, limit));
        return response(page);
    }

    @Override
    @Transactional
    public RatingModel rate(UUID storyId, BigDecimal score) {
        story(storyId);
        if (score == null || score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0) {
            throw new InvalidStoryDataException("Rating must be between 0 and 10");
        }
        UUID userId = authService.getAuthUserId();
        RatingModel rating = stories.saveRating(RatingModel.builder().id(UUID.randomUUID()).storyId(storyId)
                .userId(userId).score(score).build());
        StoryModel ratedStory = story(storyId);
        ratedStory.setRatingCount(Math.toIntExact(stories.ratingCount(storyId)));
        ratedStory.setRatingSum(stories.ratingSum(storyId));
        ratedStory.setTotalRating(stories.averageRating(storyId));
        stories.save(ratedStory);
        return rating;
    }

    @Override
    public RatingModel rating(UUID storyId) {
        story(storyId);
        return stories.rating(storyId, authService.getAuthUserId()).orElse(null);
    }

    @Override
    @Transactional
    public FavoriteModel favorite(UUID storyId, boolean add) {
        StoryModel story = story(storyId);
        UUID userId = authService.getAuthUserId();
        if (add) {
            FavoriteModel favorite = stories.saveFavorite(FavoriteModel.builder().id(UUID.randomUUID())
                    .storyId(storyId).userId(userId).build());
            story.setTotalFavorites(Math.toIntExact(stories.countFavorites(storyId)));
            stories.save(story);
            return favorite;
        }
        stories.deleteFavorite(storyId, userId);
        story.setTotalFavorites(Math.toIntExact(stories.countFavorites(storyId)));
        stories.save(story);
        return FavoriteModel.builder().storyId(storyId).userId(userId).build();
    }

    @Override
    public boolean favoriteExists(UUID storyId) {
        story(storyId);
        return stories.favoriteExists(storyId, authService.getAuthUserId());
    }

    @Override
    public PageResponse<StoryModel> favorites(int pageNumber, int pageSize) {
        return response(stories.favorites(authService.getAuthUserId(), pagination(pageNumber, pageSize, null, null)));
    }

    @Override
    @Transactional
    public ViewModel createView(UUID storyId) {
        StoryModel story = story(storyId);
        UUID userId = authService.getAuthUserId();
        ViewModel view = stories.view(storyId, userId)
                .orElseGet(() -> ViewModel.builder().id(UUID.randomUUID()).storyId(storyId).userId(userId).build());
        ViewModel saved = stories.saveView(view);
        story.setTotalViews(Math.toIntExact(stories.countViews(storyId)));
        stories.save(story);
        return saved;
    }

    @Override
    @Transactional
    public StoryModel setCover(UUID id, String url, String publicId) {
        StoryModel story = requireOwnedStory(id);
        story.setCoverUrl(url);
        story.setCoverPublicId(publicId);
        return stories.save(story);
    }

    @Override
    @Transactional
    public StoryModel removeCover(UUID id) {
        StoryModel story = requireOwnedStory(id);
        story.setCoverUrl("");
        story.setCoverPublicId(null);
        return stories.save(story);
    }

    @Override
    @Transactional
    public void updateChapterCount(UUID storyId, int count) {
        StoryModel story = story(storyId);
        story.setTotalChapters(count);
        stories.save(story);
    }

    @Override
    @Transactional
    public void updateCommentCount(UUID storyId, int count) {
        StoryModel story = story(storyId);
        story.setTotalComments(count);
        stories.save(story);
    }

    private StoryModel requireOwnedStory(UUID id) {
        StoryModel story = story(id);
        if (!authService.getAuthUserId().equals(story.getUserId())) throw new StoryOwnershipException();
        return story;
    }

    private BasicInfoModel resolveGenre(BasicInfoModel genre) {
        if (genre == null) throw new InvalidStoryDataException("A primary genre is required");
        if (genre.getName() != null && !genre.getName().isBlank()) return genreByName(genre.getName().trim());
        if (genre.getId() != null) return genreById(genre.getId());
        throw new InvalidStoryDataException("A primary genre is required");
    }

    private BasicInfoModel resolveOptionalGenre(BasicInfoModel genre) {
        if (genre == null || (genre.getName() == null && genre.getId() == null)) return null;
        return genre.getName() != null ? resolveGenre(genre) : genreById(genre.getId());
    }

    private List<BasicInfoModel> resolveTags(List<BasicInfoModel> inputTags) {
        if (inputTags == null) return List.of();
        List<BasicInfoModel> resolved = new ArrayList<>();
        for (BasicInfoModel inputTag : inputTags) {
            if (inputTag == null || inputTag.getName() == null || inputTag.getName().isBlank()) continue;
            String name = inputTag.getName().trim();
            BasicInfoModel tag = stories.tagByName(name).orElseGet(() -> stories.saveTag(
                    BasicInfoModel.builder().id(UUID.randomUUID()).name(name).build()));
            if (resolved.stream().noneMatch(existing -> existing.getId().equals(tag.getId()))) resolved.add(tag);
        }
        return resolved;
    }

    private void validateStoryInput(StoryModel input, boolean creating) {
        if (input == null) throw new InvalidStoryDataException("Story data is required");
        if (creating && (input.getTitle() == null || input.getTitle().isBlank())) {
            throw new InvalidStoryDataException("title is required");
        }
        if (input.getTitle() != null && (input.getTitle().isBlank() || input.getTitle().trim().length() > 120)) {
            throw new InvalidStoryDataException("Title must contain between 1 and 120 characters");
        }
        if (creating && (input.getDescription() == null || input.getDescription().isBlank())) {
            throw new InvalidStoryDataException("description is required");
        }
        if (input.getDescription() != null && input.getDescription().isBlank()) {
            throw new InvalidStoryDataException("description cannot be blank");
        }
        if (input.getStatus() != null && !STORY_STATUSES.contains(input.getStatus())) {
            throw new InvalidStoryDataException("Invalid story status");
        }
        if (creating && input.getGenre() == null) throw new InvalidStoryDataException("A primary genre is required");
    }

    private PaginationRequest pagination(Integer pageNumber, Integer pageSize, Integer offset, Integer limit) {
        int size = limit == null ? (pageSize == null ? 20 : pageSize) : limit;
        if (size < 1 || (offset != null && offset < 0)) throw new InvalidStoryDataException("Invalid pagination values");
        int page = offset == null ? (pageNumber == null ? 0 : pageNumber) : offset / size;
        if (page < 0) throw new InvalidStoryDataException("Invalid pagination values");
        return PaginationRequest.builder().pageNumber(page).pageSize(Math.min(size, MAX_PAGE_SIZE)).build();
    }

    private <T> PageResponse<T> response(PaginationResult<T> result) {
        return PageResponse.<T>builder().data(result.getContent()).metaData(result.toMetaData()).build();
    }
}
