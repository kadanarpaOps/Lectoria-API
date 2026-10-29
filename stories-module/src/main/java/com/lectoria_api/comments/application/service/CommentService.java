package com.lectoria_api.comments.application.service;

import com.lectoria_api.chapters.domain.model.ChapterModel;
import com.lectoria_api.chapters.domain.ports.input.ChapterUseCases;
import com.lectoria_api.comments.domain.exceptions.CommentNotFoundException;
import com.lectoria_api.comments.domain.exceptions.CommentOwnershipException;
import com.lectoria_api.comments.domain.exceptions.InvalidCommentDataException;
import com.lectoria_api.comments.domain.model.CommentFilters;
import com.lectoria_api.comments.domain.model.CommentModel;
import com.lectoria_api.comments.domain.ports.input.CommentUseCases;
import com.lectoria_api.comments.domain.ports.output.CommentRepositoryPort;
import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.users.domain.ports.input.SharedAuthUseCases;
import com.lectoria_api.users.domain.ports.input.SharedUserUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentUseCases {
    private static final int MAX_PAGE_SIZE = 100;
    private final CommentRepositoryPort comments;
    private final SharedAuthUseCases authService;
    private final SharedUserUseCases users;
    private final StoryUseCases stories;
    private final ChapterUseCases chapters;

    @Override
    @Transactional
    public CommentModel createComment(CommentModel input) {
        validateComment(input, true);
        UUID userId = authService.getAuthUserId();
        String username = users.findUserById(userId).getUsername();
        UUID storyId = input.getStoryId();
        UUID chapterId = input.getChapterId();
        if (chapterId != null) {
            ChapterModel chapter = chapters.chapter(chapterId);
            if (storyId != null && !storyId.equals(chapter.getStoryId())) {
                throw new InvalidCommentDataException("The chapter does not belong to the provided story");
            }
            storyId = chapter.getStoryId();
        }
        stories.story(storyId);
        CommentModel comment = CommentModel.builder().id(UUID.randomUUID()).username(username)
                .userId(userId.toString()).content(input.getContent().trim()).likes(0)
                .storyId(storyId).chapterId(chapterId).build();
        CommentModel saved = comments.save(comment);
        refreshCounts(saved);
        return saved;
    }

    @Override
    public CommentModel comment(UUID id) {
        CommentModel comment = comments.findById(id).orElseThrow(CommentNotFoundException::new);
        if (comment.getStoryId() != null) stories.story(comment.getStoryId());
        else if (comment.getChapterId() != null) chapters.chapter(comment.getChapterId());
        return comment;
    }

    @Override
    public PageResponse<CommentModel> comments(CommentFilters filters) {
        if (filters == null || (filters.getStoryId() == null) == (filters.getChapterId() == null)) {
            throw new InvalidCommentDataException("Provide exactly one of storyId or chapterId");
        }
        if (filters.getStoryId() != null) stories.story(filters.getStoryId());
        if (filters.getChapterId() != null) chapters.chapter(filters.getChapterId());
        PaginationResult<CommentModel> page = comments.comments(filters,
                pagination(filters.getPageNumber(), filters.getPageSize(), filters.getOffset(), filters.getLimit()));
        return PageResponse.<CommentModel>builder().data(page.getContent()).metaData(page.toMetaData()).build();
    }

    @Override
    @Transactional
    public CommentModel updateComment(UUID id, String content) {
        CommentModel comment = ownedComment(id);
        if (content == null || content.isBlank()) throw new InvalidCommentDataException("content is required");
        comment.setContent(content.trim());
        return comments.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID id) {
        CommentModel comment = ownedComment(id);
        comments.delete(comment);
        refreshCounts(comment);
    }

    @Override
    @Transactional
    public CommentModel toggleLike(UUID commentId) {
        CommentModel comment = comment(commentId);
        UUID userId = authService.getAuthUserId();
        if (comments.likeExists(commentId, userId)) comments.deleteLike(commentId, userId);
        else comments.saveLike(commentId, userId);
        comment.setLikes(Math.toIntExact(comments.countLikes(commentId)));
        return comments.save(comment);
    }

    private CommentModel ownedComment(UUID id) {
        CommentModel comment = comment(id);
        if (!comment.getUserId().equals(authService.getAuthUserId().toString())) {
            throw new CommentOwnershipException();
        }
        return comment;
    }

    private void refreshCounts(CommentModel comment) {
        UUID storyId = comment.getStoryId();
        if (storyId == null && comment.getChapterId() != null) {
            storyId = chapters.chapter(comment.getChapterId()).getStoryId();
        }
        if (storyId != null) {
            stories.updateCommentCount(storyId, Math.toIntExact(comments.countByStoryId(storyId)));
        }
        if (comment.getChapterId() != null) {
            chapters.updateCommentCount(comment.getChapterId(),
                    Math.toIntExact(comments.countByChapterId(comment.getChapterId())));
        }
    }

    private void validateComment(CommentModel input, boolean creating) {
        if (input == null) throw new InvalidCommentDataException("Comment data is required");
        if (creating && (input.getStoryId() == null && input.getChapterId() == null)) {
            throw new InvalidCommentDataException("storyId or chapterId is required");
        }
        if (creating && (input.getContent() == null || input.getContent().isBlank())) {
            throw new InvalidCommentDataException("content is required");
        }
    }

    private PaginationRequest pagination(Integer pageNumber, Integer pageSize, Integer offset, Integer limit) {
        int size = limit == null ? (pageSize == null ? 20 : pageSize) : limit;
        if (size < 1 || (offset != null && offset < 0)) throw new InvalidCommentDataException("Invalid pagination values");
        int page = offset == null ? (pageNumber == null ? 0 : pageNumber) : offset / size;
        if (page < 0) throw new InvalidCommentDataException("Invalid pagination values");
        return PaginationRequest.builder().pageNumber(page).pageSize(Math.min(size, MAX_PAGE_SIZE)).build();
    }
}
