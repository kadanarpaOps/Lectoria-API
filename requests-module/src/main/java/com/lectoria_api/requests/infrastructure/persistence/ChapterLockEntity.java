package com.lectoria_api.requests.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_CHAPTER_EDIT_LOCKS")
@Getter
@Setter
@NoArgsConstructor
public class ChapterLockEntity {
    @Id
    @Column(name = "CHAPTER_ID")
    private UUID chapterId;
    @Column(name = "USER_ID", nullable = false)
    private UUID userId;
    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;
}
