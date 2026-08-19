package com.inventory_management.user.service;

import com.inventory_management.business.entity.Business;
import com.inventory_management.business.exception.BusinessNotFoundException;
import com.inventory_management.business.repository.BusinessRepository;
import com.inventory_management.user.dto.CreateUserRequest;
import com.inventory_management.user.dto.UpdateUserRequest;
import com.inventory_management.user.dto.UserResponse;
import com.inventory_management.user.entity.User;
import com.inventory_management.user.exception.DuplicateEmailException;
import com.inventory_management.user.exception.UserNotFoundException;
import com.inventory_management.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BusinessRepository businessRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        Business business = businessRepository.findById(request.businessId())
                .orElseThrow(() -> new BusinessNotFoundException(request.businessId()));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(true);
        user.setBusiness(business);

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = findByIdOrThrow(id);

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse activateUser(UUID id) {
        User user = findByIdOrThrow(id);
        user.setActive(true);
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse deactivateUser(UUID id) {
        User user = findByIdOrThrow(id);
        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    private User findByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getBusiness().getId(),
                user.getBusiness().getName()
        );
    }
}
