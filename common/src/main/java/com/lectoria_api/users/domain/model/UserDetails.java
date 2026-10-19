package com.lectoria_api.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder @ToString
@Getter
@Setter
public class UserDetails {

    private UUID userDetailsId;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private boolean registeredVerified;
    private String profileImageUrl;

}
