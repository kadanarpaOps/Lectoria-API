package com.lectoria_api.users.infrastructure.output.persistence.mapper;

import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapper {

    private final RolePersistenceMapper roleMapper;

    public UserEntity toEntity(UserModel userModel) {
        if (userModel == null) return null;
        return UserEntity.builder()
                .userId(userModel.getUserId())
                .username(userModel.getUsername())
                .userEmail(userModel.getUserEmail())
                .userEnabled(userModel.isUserEnabled())
                .userRoles(
                        new ArrayList<>(
                                userModel.getUserRoles().stream().map(
                                        roleMapper::toEntity
                                ).toList()
                        )
                )
                .build();
    }

    public UserModel toModel(UserEntity userEntity) {
        if (userEntity == null) return null;
        return UserModel.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUsername())
                .userEmail(userEntity.getUserEmail())
                .userEnabled(userEntity.isUserEnabled())
                .userRoles(
                        new ArrayList<>(
                                userEntity.getUserRoles().stream().map(
                                        roleMapper::toModel
                                ).toList()
                        )
                )
                .build();
    }

}
