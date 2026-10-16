package com.lectoria_api.users.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

import static com.lectoria_api.common.domain.constants.Constants.TIME_ZONE;

@Entity
@Table(name = "T_USER_DETAILS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_DETAILS_ID")
    private UUID userDetailsId;
    @Column(name = "USER_CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "USER_UPDATED_AT")
    private LocalDateTime lastUpdatedAt;
    @Column(name = "USER_REGISTERED_VERIFIED")
    private boolean registeredVerified;
    @Column(name = "USER_PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(TIME_ZONE);
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedAt = LocalDateTime.now(TIME_ZONE);
    }

}
