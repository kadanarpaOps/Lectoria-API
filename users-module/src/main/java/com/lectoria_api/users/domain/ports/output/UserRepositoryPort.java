package com.lectoria_api.users.domain.ports.output;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    PaginationResult<UserModel> pageUsers(PaginationRequest request, UserFilters filters);
    Optional<UserModel> findUserById(UUID userId);
    Optional<UserModel> findUserByEmail(String email);
    boolean existsByFilters(UserFilters filters);
    void save(UserModel user);

}
