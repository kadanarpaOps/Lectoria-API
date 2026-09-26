package com.lectoria_api.users.infrastructure.output.persistence.mapper;

import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.infrastructure.output.persistence.entities.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RolePersistenceMapper {

    public RoleEntity toEntity(RoleModel roleModel) {
        if (roleModel == null) return null;
        return RoleEntity.builder()
                .roleId(roleModel.getRoleId())
                .roleName(roleModel.getRoleName())
                .build();
    }

    public RoleModel toModel(RoleEntity roleEntity) {
        if (roleEntity == null) return null;
        return RoleModel.builder()
                .roleId(roleEntity.getRoleId())
                .roleName(roleEntity.getRoleName())
                .build();
    }

}
