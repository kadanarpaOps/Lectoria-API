package com.lectoria_api.requests.infrastructure.persistence;

import com.lectoria_api.requests.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<RequestEntity, UUID> {
    List<RequestEntity> findAllBySourceUserIdOrTargetUserIdOrderByCreatedAtDesc(UUID sourceUserId, UUID targetUserId);
    List<RequestEntity> findAllByTypeAndStatusOrderByCreatedAtDesc(com.lectoria_api.requests.domain.RequestType type, RequestStatus status);
    boolean existsByTypeAndStatusAndSourceUserIdAndTargetUserIdAndStoryId(
            com.lectoria_api.requests.domain.RequestType type, RequestStatus status,
            UUID sourceUserId, UUID targetUserId, UUID storyId);
}
