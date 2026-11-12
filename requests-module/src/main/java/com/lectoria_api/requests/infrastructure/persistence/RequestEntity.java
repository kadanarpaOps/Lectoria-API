package com.lectoria_api.requests.infrastructure.persistence;

import com.lectoria_api.requests.domain.RequestStatus;
import com.lectoria_api.requests.domain.RequestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_USER_REQUESTS")
@Getter
@Setter
@NoArgsConstructor
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "REQUEST_ID")
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "REQUEST_TYPE", nullable = false)
    private RequestType type;
    @Enumerated(EnumType.STRING)
    @Column(name = "REQUEST_STATUS", nullable = false)
    private RequestStatus status;
    @Column(name = "SOURCE_USER_ID", nullable = false)
    private UUID sourceUserId;
    @Column(name = "TARGET_USER_ID")
    private UUID targetUserId;
    @Column(name = "STORY_ID")
    private UUID storyId;
    @Column(name = "COAUTHOR_USER_ID")
    private UUID coauthorUserId;
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "RESOLVED_AT")
    private LocalDateTime resolvedAt;
}
