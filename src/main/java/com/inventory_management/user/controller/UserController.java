package com.inventory_management.user.controller;

import com.inventory_management.user.dto.CreateUserRequest;
import com.inventory_management.user.dto.UpdateUserRequest;
import com.inventory_management.user.dto.UserResponse;
import com.inventory_management.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}
