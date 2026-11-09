package com.lectoria_api.chapters.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_CHAPTERS", uniqueConstraints = @UniqueConstraint(columnNames = {"STORY_ID", "CHAPTER_ORDER"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterEntity {
    @Id
    @Column(name = "CHAPTER_ID")
    private UUID id;
    @Column(name = "CHAPTER_TITLE", nullable = false, columnDefinition = "TEXT")
    private String title;
    @Column(name = "CHAPTER_ORDER", nullable = false)
    private Integer order;
    @Column(name = "CHAPTER_CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "CHAPTER_HIDDEN", nullable = false)
    private Boolean hidden;
    @Column(name = "CHAPTER_TOTAL_COMMENTS", nullable = false)
    private Integer totalComments;
    @Column(name = "STORY_ID", nullable = false)
    private UUID storyId;
    @Column(name = "CHAPTER_CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "CHAPTER_UPDATED_AT")
    private LocalDateTime updatedAt;
    @Column(name = "CHAPTER_LAST_ACTIVITY_AT")
    private LocalDateTime lastActivityAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (lastActivityAt == null) lastActivityAt = createdAt;
        if (hidden == null) hidden = true;
        if (totalComments == null) totalComments = 0;
    }

    @PreUpdate
    void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        updatedAt = now;
        lastActivityAt = now;
    }
}
