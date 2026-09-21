package com.lectoria_api.users.infrastructure.output.adapter;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.ports.output.RoleRepositoryPort;
import com.lectoria_api.users.infrastructure.output.persistence.entities.RoleEntity;
import com.lectoria_api.users.infrastructure.output.persistence.mapper.RolePersistenceMapper;
import com.lectoria_api.users.infrastructure.output.persistence.repository.JpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final JpaRoleRepository roleRepository;
    private final RolePersistenceMapper roleMapper;

    @Override
    public PaginationResult<RoleModel> pageRoles(PaginationRequest paginationRequest, String roleName) {
        Pageable pageable = PageRequest.of(
                paginationRequest.getPageNumber(),
                paginationRequest.getPageSize()
        );

        Page<RoleEntity> pageResult = roleRepository.pageRoles(roleName, pageable);

        List<RoleModel> roleModels = pageResult.getContent().stream().map(
                roleMapper::toModel
        ).toList();

        return PaginationResult.<RoleModel>builder()
                .content(roleModels)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Override
    public Optional<RoleModel> findRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName).map(roleMapper::toModel);
    }

}
