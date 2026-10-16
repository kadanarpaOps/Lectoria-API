package com.lectoria_api.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder @ToString
@Getter
@Setter
public class UserDetails {

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private boolean registeredVerified;
    private String profileImageUrl;
    private UserModel user;

}
