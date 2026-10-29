package com.lectoria_api.stories.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "T_STORIES", uniqueConstraints = @UniqueConstraint(name = "UK_STORY_TITLE", columnNames = "STORY_TITLE"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryEntity {
    @Id
    @Column(name = "STORY_ID")
    private UUID id;

    @Column(name = "STORY_TITLE", nullable = false, length = 120)
    private String title;

    @Column(name = "STORY_COVER_URL", nullable = false, columnDefinition = "TEXT")
    private String coverUrl;

    @Column(name = "STORY_COVER_PUBLIC_ID", columnDefinition = "TEXT")
    private String coverPublicId;

    @Column(name = "STORY_DESCRIPTION", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "STORY_TOTAL_RATING", nullable = false, precision = 4, scale = 2)
    private BigDecimal totalRating;

    @Column(name = "STORY_RATING_SUM", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratingSum;

    @Column(name = "STORY_RATING_COUNT", nullable = false)
    private Integer ratingCount;

    @Column(name = "STORY_TOTAL_CHAPTERS", nullable = false)
    private Integer totalChapters;

    @Column(name = "STORY_HIDDEN", nullable = false)
    private Boolean hidden;

    @Column(name = "STORY_STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "STORY_TOTAL_VIEWS", nullable = false)
    private Integer totalViews;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;

    @Column(name = "STORY_TOTAL_FAVORITES", nullable = false)
    private Integer totalFavorites;

    @Column(name = "STORY_TOTAL_COMMENTS", nullable = false)
    private Integer totalComments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GENRE_ID", nullable = false)
    private GenreEntity genre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SECONDARY_GENRE_ID")
    private GenreEntity secondaryGenre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "T_STORY_TAGS",
            joinColumns = @JoinColumn(name = "STORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    @Builder.Default
    private List<TagEntity> tags = new ArrayList<>();

    @Column(name = "STORY_CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "STORY_UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "STORY_LAST_ACTIVITY_AT")
    private LocalDateTime lastActivityAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (lastActivityAt == null) lastActivityAt = createdAt;
        if (coverUrl == null) coverUrl = "";
        if (totalRating == null) totalRating = BigDecimal.ZERO;
        if (ratingSum == null) ratingSum = BigDecimal.ZERO;
        if (ratingCount == null) ratingCount = 0;
        if (totalChapters == null) totalChapters = 0;
        if (hidden == null) hidden = true;
        if (status == null) status = "IN_PROGRESS";
        if (totalViews == null) totalViews = 0;
        if (totalFavorites == null) totalFavorites = 0;
        if (totalComments == null) totalComments = 0;
    }

    @PreUpdate
    void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        updatedAt = now;
        lastActivityAt = now;
    }
}
