package com.inventory_management.user.service;

import com.inventory_management.user.dto.CreateUserRequest;
import com.inventory_management.user.dto.UpdateUserRequest;
import com.inventory_management.user.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    UserResponse activateUser(UUID id);

    UserResponse deactivateUser(UUID id);
}
