package com.lectoria_api.users.infrastructure.input.rest.mapper;

import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.model.UserDetails;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.RegisterUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.in.UpdateUserProfileDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.ResUserDTO;
import com.lectoria_api.users.infrastructure.input.rest.dto.out.ResUserDetailsDTO;
import org.springframework.stereotype.Component;

@Component
public class UserRestMapper {

    public UserModel toModel(RegisterUserDTO userDTO) {
        if (userDTO == null) return null;
        return UserModel.builder()
                .username(userDTO.getUsername())
                .userEmail(userDTO.getEmail())
                .userPassword(userDTO.getPassword())
                .userRoles(userDTO.getRoleNames().stream().map(
                        roleName -> RoleModel.builder().roleName(roleName).build()
                ).toList())
                .build();
    }

    public UserModel toModel(UpdateUserProfileDTO userDTO) {
        if (userDTO == null) return null;
        return UserModel.builder()
                .username(userDTO.getNewUsername())
                .build();
    }

    public ResUserDTO toResDto(UserModel userModel) {
        if (userModel == null) return null;
        ResUserDetailsDTO resUserDetails = new ResUserDetailsDTO();
        UserDetails userDetailsModel = userModel.getUserDetails();
        if (userDetailsModel != null) {
            resUserDetails = ResUserDetailsDTO.builder()
                    .createdAt(userDetailsModel.getCreatedAt())
                    .lastUpdatedAt(userDetailsModel.getLastUpdatedAt())
                    .profileImageUrl(userDetailsModel.getProfileImageUrl())
                    .registeredVerified(userDetailsModel.isRegisteredVerified())
                    .build();
        }
        return ResUserDTO.builder()
                .username(userModel.getUsername())
                .email(userModel.getUserEmail())
                .userRoles(userModel.getUserRoles().stream().map(
                        RoleModel::getRoleName
                ).toList())
                .userDetails(resUserDetails)
                .build();
    }

}
