package com.inventory_management.user.controller;

import tools.jackson.databind.ObjectMapper;
import com.inventory_management.common.exception.GlobalExceptionHandler;
import com.inventory_management.user.UserRole;
import com.inventory_management.user.dto.CreateUserRequest;
import com.inventory_management.user.dto.UpdateUserRequest;
import com.inventory_management.user.dto.UserResponse;
import com.inventory_management.user.exception.DuplicateEmailException;
import com.inventory_management.user.exception.UserNotFoundException;
import com.inventory_management.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final UUID userId     = UUID.randomUUID();
    private final UUID businessId = UUID.randomUUID();
    private UserResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        sampleResponse = new UserResponse(
                userId,
                "Ade Bello",
                "ade@example.com",
                UserRole.OWNER,
                true,
                Instant.now(),
                businessId,
                "Ade Stores"
        );
    }

    // =========================================================================
    // POST /api/v1/users
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/users: 201 with valid request")
    void createUser_valid_returns201() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "Ade Bello", "ade@example.com", "secret123", UserRole.OWNER, businessId
        );
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Ade Bello"))
                .andExpect(jsonPath("$.email").value("ade@example.com"))
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.businessId").value(businessId.toString()))
                .andExpect(jsonPath("$.businessName").value("Ade Stores"));
    }

    @Test
    @DisplayName("POST /api/v1/users: 400 when required fields are missing")
    void createUser_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/users: 400 when email is malformed")
    void createUser_invalidEmail_returns400() throws Exception {
        String body = """
                {"name":"Ade Bello","email":"not-an-email","password":"secret123","role":"OWNER","businessId":"%s"}
                """.formatted(businessId);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users: 400 when businessId is missing")
    void createUser_missingBusinessId_returns400() throws Exception {
        String body = """
                {"name":"Ade Bello","email":"ade@example.com","password":"secret123","role":"OWNER"}
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.businessId").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users: 409 when email is already taken")
    void createUser_duplicateEmail_returns409() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "Ade Bello", "ade@example.com", "secret123", UserRole.OWNER, businessId
        );
        when(userService.createUser(any())).thenThrow(new DuplicateEmailException("ade@example.com"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("A user with this email already exists: ade@example.com"));
    }

    // =========================================================================
    // GET /api/v1/users/{id}
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/users/{id}: 200 with valid UUID for existing user")
    void getUserById_found_returns200() throws Exception {
        when(userService.getUserById(userId)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("ade@example.com"))
                .andExpect(jsonPath("$.businessId").value(businessId.toString()))
                .andExpect(jsonPath("$.businessName").value("Ade Stores"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id}: 404 for non-existent user")
    void getUserById_notFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(userService.getUserById(unknownId)).thenThrow(new UserNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/users/" + unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with id: " + unknownId));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id}: 400 for invalid UUID format")
    void getUserById_invalidUuid_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/users/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // =========================================================================
    // GET /api/v1/users
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/users: 200 with list of users")
    void getAllUsers_returns200WithList() throws Exception {
        UserResponse second = new UserResponse(
                UUID.randomUUID(), "Chioma Obi", "chioma@example.com",
                UserRole.MANAGER, true, Instant.now(), businessId, "Ade Stores"
        );
        when(userService.getAllUsers()).thenReturn(List.of(sampleResponse, second));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("ade@example.com"))
                .andExpect(jsonPath("$[1].email").value("chioma@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users: 200 with empty array when no users exist")
    void getAllUsers_returnsEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =========================================================================
    // PUT /api/v1/users/{id}
    // =========================================================================

    @Test
    @DisplayName("PUT /api/v1/users/{id}: 200 with updated user on valid request")
    void updateUser_valid_returns200() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Ade Updated", "ade.updated@example.com", UserRole.MANAGER);
        UserResponse updated = new UserResponse(
                userId, "Ade Updated", "ade.updated@example.com",
                UserRole.MANAGER, true, Instant.now(), businessId, "Ade Stores"
        );
        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ade Updated"))
                .andExpect(jsonPath("$.email").value("ade.updated@example.com"))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id}: 400 when required fields are missing")
    void updateUser_missingFields_returns400() throws Exception {
        mockMvc.perform(put("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id}: 404 when user does not exist")
    void updateUser_notFound_returns404() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Ade Updated", "ade.updated@example.com", UserRole.MANAGER);
        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(put("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id}: 409 when new email is already taken")
    void updateUser_duplicateEmail_returns409() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Ade Bello", "taken@example.com", UserRole.OWNER);
        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenThrow(new DuplicateEmailException("taken@example.com"));

        mockMvc.perform(put("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // =========================================================================
    // PATCH /api/v1/users/{id}/activate
    // =========================================================================

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/activate: 200 with active=true")
    void activateUser_returns200() throws Exception {
        UserResponse activated = new UserResponse(
                userId, "Ade Bello", "ade@example.com",
                UserRole.OWNER, true, Instant.now(), businessId, "Ade Stores"
        );
        when(userService.activateUser(userId)).thenReturn(activated);

        mockMvc.perform(patch("/api/v1/users/" + userId + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/activate: 404 when user does not exist")
    void activateUser_notFound_returns404() throws Exception {
        when(userService.activateUser(userId)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(patch("/api/v1/users/" + userId + "/activate"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // =========================================================================
    // PATCH /api/v1/users/{id}/deactivate
    // =========================================================================

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/deactivate: 200 with active=false")
    void deactivateUser_returns200() throws Exception {
        UserResponse deactivated = new UserResponse(
                userId, "Ade Bello", "ade@example.com",
                UserRole.OWNER, false, Instant.now(), businessId, "Ade Stores"
        );
        when(userService.deactivateUser(userId)).thenReturn(deactivated);

        mockMvc.perform(patch("/api/v1/users/" + userId + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/deactivate: 404 when user does not exist")
    void deactivateUser_notFound_returns404() throws Exception {
        when(userService.deactivateUser(userId)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(patch("/api/v1/users/" + userId + "/deactivate"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
