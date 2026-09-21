package com.lectoria_api.users.infrastructure.output.adapter;

import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.users.domain.model.UserFilters;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.output.UserRepositoryPort;
import com.lectoria_api.users.infrastructure.output.persistence.entities.UserEntity;
import com.lectoria_api.users.infrastructure.output.persistence.mapper.UserPersistenceMapper;
import com.lectoria_api.users.infrastructure.output.persistence.repository.JpaUserRepository;
import com.lectoria_api.users.infrastructure.output.persistence.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository userRepository;
    private final UserPersistenceMapper userMapper;

    @Override
    public PaginationResult<UserModel> pageUsers(PaginationRequest request, UserFilters filters) {
        Pageable pageable = PageRequest.of(
                request.getPageNumber(),
                request.getPageSize()
        );

        Page<UserEntity> pageResult = userRepository.findAll(
                UserSpecification.withFilters(filters),
                pageable
        );

        List<UserModel> userModels = pageResult.getContent().stream().map(
                userMapper::toModel
        ).toList();

        return PaginationResult.<UserModel>builder()
                .content(userModels)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Override
    public Optional<UserModel> findUserById(UUID userId) {
        return userRepository.findById(userId).map(userMapper::toModel);
    }

    @Override
    public Optional<UserModel> findUserByEmail(String email) {
        return userRepository.findByUserEmail(email).map(userMapper::toModel);
    }

    @Override
    public boolean existsByFilters(UserFilters filters) {
        return userRepository.exists(
                UserSpecification.withFilters(filters)
        );
    }

    @Override
    public void save(UserModel user) {
        userRepository.save(userMapper.toEntity(user));
    }
}
