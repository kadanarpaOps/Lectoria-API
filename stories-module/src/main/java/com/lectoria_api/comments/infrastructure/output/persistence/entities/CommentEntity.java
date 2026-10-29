package com.lectoria_api.comments.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_COMMENTS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
    @Id
    @Column(name = "COMMENT_ID")
    private UUID id;
    @Column(name = "COMMENT_USERNAME", nullable = false, length = 120)
    private String username;
    @Column(name = "USER_ID", nullable = false, length = 100)
    private String userId;
    @Column(name = "COMMENT_CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "COMMENT_LIKES", nullable = false)
    private Integer likes;
    @Column(name = "STORY_ID")
    private UUID storyId;
    @Column(name = "CHAPTER_ID")
    private UUID chapterId;
    @Column(name = "COMMENT_CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "COMMENT_UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (likes == null) likes = 0;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
