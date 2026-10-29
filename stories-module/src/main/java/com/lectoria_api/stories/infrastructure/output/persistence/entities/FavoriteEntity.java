package com.lectoria_api.stories.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "T_FAVORITE_STORIES", uniqueConstraints = @UniqueConstraint(columnNames = {"STORY_ID", "USER_ID"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEntity {
    @Id
    @Column(name = "FAVORITE_STORY_ID")
    private UUID id;
    @Column(name = "STORY_ID", nullable = false)
    private UUID storyId;
    @Column(name = "USER_ID", nullable = false)
    private UUID userId;
    @Column(name = "FAVORITE_CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
