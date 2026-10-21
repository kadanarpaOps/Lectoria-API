package com.lectoria_api.users.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
public class UserFilters {

    private String username;
    private String userEmail;
    private List<String> userRolesNames;
    private List<String> excludedRolesNames;
    private Boolean userStatus;
    private Boolean isRegistryVerified;
    private List<UUID> excludedUsers;

}
