package com.lectoria_api.stories.infrastructure.output.adapter;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.stories.domain.model.BasicInfoModel;
import com.lectoria_api.stories.domain.model.FavoriteModel;
import com.lectoria_api.stories.domain.model.RatingModel;
import com.lectoria_api.stories.domain.model.StoryFilters;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.model.ViewModel;
import com.lectoria_api.stories.domain.exceptions.StoryNotFoundException;
import com.lectoria_api.stories.domain.ports.output.StoryRepositoryPort;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.FavoriteEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.GenreEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.RatingEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.StoryEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.TagEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.entities.ViewEntity;
import com.lectoria_api.stories.infrastructure.output.persistence.mapper.StoryPersistenceMapper;
import com.lectoria_api.stories.infrastructure.output.persistence.repository.JpaFavoriteRepository;
import com.lectoria_api.stories.infrastructure.output.persistence.repository.JpaGenreRepository;
import com.lectoria_api.stories.infrastructure.output.persistence.repository.JpaRatingRepository;
import com.lectoria_api.stories.infrastructure.output.persistence.repository.JpaStoryRepository;
import com.lectoria_api.stories.infrastructure.output.persistence.repository.JpaTagRepository;
import com.lectoria_api.stories.infrastructure.output.persistence.repository.JpaViewRepository;
import com.lectoria_api.stories.infrastructure.output.persistence.specification.StorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoryRepositoryAdapter implements StoryRepositoryPort {
    private final JpaStoryRepository stories;
    private final JpaGenreRepository genres;
    private final JpaTagRepository tags;
    private final JpaRatingRepository ratings;
    private final JpaFavoriteRepository favorites;
    private final JpaViewRepository views;
    private final StoryPersistenceMapper mapper;

    @Override
    @Transactional
    public StoryModel save(StoryModel story) {
        GenreEntity genre = genres.getReferenceById(story.getGenre().getId());
        GenreEntity secondaryGenre = story.getSecondaryGenre() == null || story.getSecondaryGenre().getId() == null
                ? null : genres.getReferenceById(story.getSecondaryGenre().getId());
        List<TagEntity> storyTags = story.getTags() == null ? List.of()
                : story.getTags().stream().map(tag -> tags.getReferenceById(tag.getId())).toList();
        return mapper.toModel(stories.save(mapper.toEntity(story, genre, secondaryGenre, storyTags)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StoryModel> findById(UUID id) {
        return stories.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StoryModel> findByTitle(String title) {
        return stories.findByTitleIgnoreCase(title).map(mapper::toModel);
    }

    @Override
    public boolean existsByTitle(String title) {
        return stories.existsByTitleIgnoreCase(title);
    }

    @Override
    public boolean existsByTitleAndIdNot(String title, UUID id) {
        return stories.existsByTitleIgnoreCaseAndIdNot(title, id);
    }

    @Override
    public void delete(UUID id) {
        stories.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<StoryModel> search(StoryFilters filters, PaginationRequest request) {
        Sort sort = Boolean.TRUE.equals(filters.getNewestFirst())
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : Sort.by(Sort.Order.desc("lastActivityAt").nullsLast(), Sort.Order.desc("createdAt"));
        Page<StoryEntity> page = stories.findAll(StorySpecification.withFilters(filters),
                PageRequest.of(request.getPageNumber(), request.getPageSize(), sort));
        return storyPagination(page.map(mapper::toModel));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StoryModel> lastModified(UUID userId) {
        return stories.findFirstByUserIdOrderByLastActivityAtDescCreatedAtDesc(userId).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BasicInfoModel> genres() {
        return genres.findAll(Sort.by("name")).stream().map(mapper::toBasicInfo).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BasicInfoModel> genreById(UUID id) {
        return genres.findById(id).map(mapper::toBasicInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BasicInfoModel> genreByName(String name) {
        return genres.findByNameIgnoreCase(name).map(mapper::toBasicInfo);
    }

    @Override
    @Transactional
    public Optional<BasicInfoModel> tagByName(String name) {
        return tags.findByNameIgnoreCase(name).map(mapper::toBasicInfo);
    }

    @Override
    @Transactional
    public BasicInfoModel saveTag(BasicInfoModel tag) {
        TagEntity entity = TagEntity.builder().id(tag.getId()).name(tag.getName()).build();
        return mapper.toBasicInfo(tags.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<BasicInfoModel> searchTags(String name, PaginationRequest request) {
        Page<TagEntity> page = tags.findByNameContainingIgnoreCase(name, PageRequest.of(request.getPageNumber(),
                request.getPageSize(), Sort.by("name")));
        return tagPagination(page.map(mapper::toBasicInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RatingModel> rating(UUID storyId, UUID userId) {
        return ratings.findByStoryIdAndUserId(storyId, userId).map(mapper::toModel);
    }

    @Override
    @Transactional
    public RatingModel saveRating(RatingModel rating) {
        RatingEntity entity = ratings.findByStoryIdAndUserId(rating.getStoryId(), rating.getUserId())
                .orElseGet(() -> mapper.toEntity(rating));
        entity.setScore(rating.getScore());
        return mapper.toModel(ratings.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public long ratingCount(UUID storyId) {
        return ratings.countByStoryId(storyId);
    }

    @Override
    @Transactional(readOnly = true)
    public java.math.BigDecimal ratingSum(UUID storyId) {
        return ratings.sumScoreByStoryId(storyId);
    }

    @Override
    @Transactional(readOnly = true)
    public java.math.BigDecimal averageRating(UUID storyId) {
        return ratings.averageScoreByStoryId(storyId);
    }

    @Override
    public boolean favoriteExists(UUID storyId, UUID userId) {
        return favorites.findByStoryIdAndUserId(storyId, userId).isPresent();
    }

    @Override
    @Transactional
    public FavoriteModel saveFavorite(FavoriteModel favorite) {
        FavoriteEntity entity = favorites.findByStoryIdAndUserId(favorite.getStoryId(), favorite.getUserId())
                .orElseGet(() -> mapper.toEntity(favorite));
        return mapper.toModel(favorites.save(entity));
    }

    @Override
    public void deleteFavorite(UUID storyId, UUID userId) {
        favorites.findByStoryIdAndUserId(storyId, userId).ifPresent(favorites::delete);
    }

    @Override
    public long countFavorites(UUID storyId) {
        return favorites.countByStoryId(storyId);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<StoryModel> favorites(UUID userId, PaginationRequest request) {
        Page<FavoriteEntity> favoritePage = favorites.findByUserIdOrderByCreatedAtDesc(userId,
                PageRequest.of(request.getPageNumber(), request.getPageSize()));
        List<StoryModel> content = favoritePage.getContent().stream()
                .map(favorite -> stories.findById(favorite.getStoryId()).map(mapper::toModel)
                        .orElseThrow(StoryNotFoundException::new)).toList();
        return PaginationResult.<StoryModel>builder().content(content).pageNumber(favoritePage.getNumber())
                .pageSize(favoritePage.getSize()).totalElements(favoritePage.getTotalElements())
                .totalPages(favoritePage.getTotalPages()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ViewModel> view(UUID storyId, UUID userId) {
        return views.findByStoryIdAndUserId(storyId, userId).map(mapper::toModel);
    }

    @Override
    @Transactional
    public ViewModel saveView(ViewModel view) {
        ViewEntity entity = views.findByStoryIdAndUserId(view.getStoryId(), view.getUserId())
                .orElseGet(() -> mapper.toEntity(view));
        return mapper.toModel(views.save(entity));
    }

    @Override
    public long countViews(UUID storyId) {
        return views.countByStoryId(storyId);
    }

    private PaginationResult<StoryModel> storyPagination(Page<StoryModel> page) {
        return PaginationResult.<StoryModel>builder().content(page.getContent()).pageNumber(page.getNumber())
                .pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).build();
    }

    private PaginationResult<BasicInfoModel> tagPagination(Page<BasicInfoModel> page) {
        return PaginationResult.<BasicInfoModel>builder().content(page.getContent()).pageNumber(page.getNumber())
                .pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).build();
    }
}
