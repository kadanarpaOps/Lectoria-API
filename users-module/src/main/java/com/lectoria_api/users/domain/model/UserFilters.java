package com.lectoria_api.users.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class UserFilters {

    private String username;
    private String userEmail;
    private List<String> userRolesNames;
    private Boolean userStatus;

}
