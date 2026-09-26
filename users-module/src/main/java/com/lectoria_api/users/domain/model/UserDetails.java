package com.lectoria_api.users.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder @ToString
@Getter
@Setter
public class UserDetails {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean registeredVerified;
    private String profileImageUrl;

}
