package com.lectoria_api.requests.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_STORY_COAUTHORS", uniqueConstraints = @UniqueConstraint(columnNames = {"STORY_ID", "USER_ID"}))
@Getter
@Setter
@NoArgsConstructor
public class CoauthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "COAUTHOR_ID")
    private UUID id;
    @Column(name = "STORY_ID", nullable = false)
    private UUID storyId;
    @Column(name = "USER_ID", nullable = false)
    private UUID userId;
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}
