package com.lectoria_api.users.application.service;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.users.domain.exceptions.business.RoleWithNameNotExistsException;
import com.lectoria_api.users.domain.model.RoleModel;
import com.lectoria_api.users.domain.ports.input.RoleUseCases;
import com.lectoria_api.users.domain.ports.output.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServicePort implements RoleUseCases {

    private final RoleRepositoryPort roleRepository;

    @Override
    public PageResponse<RoleModel> pageRoles(PaginationRequest paginationRequest, String roleName) {
        PaginationResult<RoleModel> pageResult = roleRepository.pageRoles(paginationRequest, roleName);

        return PageResponse.<RoleModel>builder()
                .data(pageResult.getContent())
                .metaData(pageResult.toMetaData())
                .build();
    }

    /**
     * This method search in the Inner BD of the Application for a <code>ROLE</code> Entity with
     * the inserted <code>ROLE_NAME</code> value
     * @param roleName the role Name to search
     * @return Optional Object with the DB Repository Result
     */
    @Override
    public RoleModel findRoleByName(String roleName) {
        Optional<RoleModel> optionalRole = roleRepository.findRoleByName(roleName);
        if (!optionalRole.isPresent()) {
            throw new RoleWithNameNotExistsException(roleName);
        }
        return optionalRole.get();
    }

}
