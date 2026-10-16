package com.lectoria_api.users.infrastructure.output.persistence.mapper;

import com.lectoria_api.users.domain.model.UserDetails;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserDetailsEntity;
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

        UserEntity userEntity = UserEntity.builder()
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

        UserDetails userDetailsModel = userModel.getUserDetails();
        UserDetailsEntity userDetailsEntity =  new UserDetailsEntity();
        if (userDetailsModel != null) {
            userDetailsEntity = UserDetailsEntity.builder()
                    .profileImageUrl(userDetailsModel.getProfileImageUrl())
                    .registeredVerified(userDetailsModel.isRegisteredVerified())
                    .user(userEntity)
                    .build();
        };

        userEntity.setUserDetails(userDetailsEntity);

        return userEntity;
    }

    public UserModel toModel(UserEntity userEntity) {
        if (userEntity == null) return null;
        UserDetailsEntity userDetailsEntity = userEntity.getUserDetails();
        UserDetails userDetailsModel = new UserDetails();
        if (userDetailsEntity != null) {
            userDetailsModel = UserDetails.builder()
                    .profileImageUrl(userDetailsEntity.getProfileImageUrl())
                    .registeredVerified(userDetailsEntity.isRegisteredVerified())
                    .createdAt(userDetailsEntity.getCreatedAt())
                    .lastUpdatedAt(userDetailsEntity.getLastUpdatedAt())
                    .build();
        };
        return UserModel.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUsername())
                .userEmail(userEntity.getUserEmail())
                .userEnabled(userEntity.isUserEnabled())
                .userDetails(userDetailsModel)
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
