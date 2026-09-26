package com.lectoria_api.users.domain.ports.output;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.users.domain.model.RoleModel;

import java.util.Optional;

public interface RoleRepositoryPort {

    PaginationResult<RoleModel> pageRoles(PaginationRequest paginationRequest, String roleName);
    Optional<RoleModel> findRoleByName(String roleName);

}
