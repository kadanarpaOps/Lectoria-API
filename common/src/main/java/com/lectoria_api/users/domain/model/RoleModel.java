package com.lectoria_api.users.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class RoleModel {

    private UUID roleId;
    private String roleName;

}
