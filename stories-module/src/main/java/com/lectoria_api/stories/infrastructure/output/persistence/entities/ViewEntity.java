package com.lectoria_api.stories.infrastructure.output.persistence.entities;

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
@Table(name = "T_VIEWS", uniqueConstraints = @UniqueConstraint(columnNames = {"STORY_ID", "USER_ID"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewEntity {
    @Id
    @Column(name = "VIEW_ID")
    private UUID id;
    @Column(name = "STORY_ID", nullable = false)
    private UUID storyId;
    @Column(name = "USER_ID", nullable = false)
    private UUID userId;
    @Column(name = "VIEW_CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "VIEW_UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
