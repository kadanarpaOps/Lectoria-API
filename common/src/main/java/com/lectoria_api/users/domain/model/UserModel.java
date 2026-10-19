package com.lectoria_api.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder @ToString
@Getter
@Setter
public class UserModel {

    private UUID userId;
    private String username;
    private String userEmail;
    private String userPassword;
    private List<RoleModel> userRoles;
    private boolean userEnabled;
    private UserDetails userDetails;

}
