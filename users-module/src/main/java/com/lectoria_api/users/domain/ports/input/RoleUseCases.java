package com.lectoria_api.users.domain.ports.input;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.users.domain.model.RoleModel;

public interface RoleUseCases {

    PageResponse<RoleModel> pageRoles(PaginationRequest paginationRequest, String roleName);
    RoleModel findRoleByName(String roleName);

}
