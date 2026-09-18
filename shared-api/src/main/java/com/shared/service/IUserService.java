package com.shared.service;


import com.shared.dto.auth.*;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserDto createUser(CreateUserRequest request);
    UserDto getUserById(UUID id);
    List<UserDto> getAllUsers();
    UserDto updateUser(UUID id, UpdateUserRequest request);
    boolean deleteUser(UUID id);

    // Отдельные варианты использования связи many-to-many User <-> Roles
    UserDto assignRole(AssignRoleRequest request);
    UserDto revokeRole(RevokeRoleRequest request);

}