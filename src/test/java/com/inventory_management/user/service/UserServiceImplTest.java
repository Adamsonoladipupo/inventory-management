package com.inventory_management.user.service;

import com.inventory_management.user.UserRole;
import com.inventory_management.user.dto.CreateUserRequest;
import com.inventory_management.user.dto.UpdateUserRequest;
import com.inventory_management.user.dto.UserResponse;
import com.inventory_management.user.entity.User;
import com.inventory_management.user.exception.DuplicateEmailException;
import com.inventory_management.user.exception.UserNotFoundException;
import com.inventory_management.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private static final UUID FIXED_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Instant FIXED_TIME = Instant.parse("2026-01-01T10:00:00Z");

    private User buildSavedUser(String name, String email, String password, UserRole role) {
        User user = new User();
        user.setId(FIXED_ID);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(FIXED_TIME);
        return user;
    }

    // =========================================================================
    // createUser
    // =========================================================================

    @Nested
    @DisplayName("createUser()")
    class CreateUserTests {

        private CreateUserRequest ownerRequest;

        @BeforeEach
        void setUp() {
            ownerRequest = new CreateUserRequest(
                    "Ade Bello",
                    "ade@example.com",
                    "secret123",
                    UserRole.OWNER
            );
        }

        @Test
        @DisplayName("should return a UserResponse with the correct id when user is created successfully")
        void shouldReturnCorrectId() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            UserResponse response = userService.createUser(ownerRequest);

            assertThat(response.id()).isEqualTo(FIXED_ID);
        }

        @Test
        @DisplayName("should return a UserResponse with the correct name when user is created successfully")
        void shouldReturnCorrectName() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            UserResponse response = userService.createUser(ownerRequest);

            assertThat(response.name()).isEqualTo("Ade Bello");
        }

        @Test
        @DisplayName("should return a UserResponse with the correct email when user is created successfully")
        void shouldReturnCorrectEmail() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            UserResponse response = userService.createUser(ownerRequest);

            assertThat(response.email()).isEqualTo("ade@example.com");
        }

        @Test
        @DisplayName("should return a UserResponse with the correct role when user is created successfully")
        void shouldReturnCorrectRole() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            UserResponse response = userService.createUser(ownerRequest);

            assertThat(response.role()).isEqualTo(UserRole.OWNER);
        }

        @Test
        @DisplayName("should return a UserResponse with active=true when user is created successfully")
        void shouldReturnActiveTrue() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            UserResponse response = userService.createUser(ownerRequest);

            assertTrue(response.active());
        }

        @Test
        @DisplayName("should return a UserResponse with a non-null createdAt when user is created successfully")
        void shouldReturnNonNullCreatedAt() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            UserResponse response = userService.createUser(ownerRequest);

            assertNotNull(response.createdAt());
            assertThat(response.createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("should persist a User entity with the correct name from the request")
        void shouldPersistCorrectName() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.createUser(ownerRequest);

            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Ade Bello");
        }

        @Test
        @DisplayName("should persist a User entity with the correct email from the request")
        void shouldPersistCorrectEmail() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.createUser(ownerRequest);

            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo("ade@example.com");
        }

        @Test
        @DisplayName("should persist a User entity with the correct password from the request")
        void shouldPersistCorrectPassword() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.createUser(ownerRequest);

            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("secret123");
        }

        @Test
        @DisplayName("should persist a User entity with the correct role from the request")
        void shouldPersistCorrectRole() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.createUser(ownerRequest);

            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getRole()).isEqualTo(UserRole.OWNER);
        }

        @Test
        @DisplayName("should persist a User entity with active=true regardless of input")
        void shouldPersistWithActiveSetToTrue() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.createUser(ownerRequest);

            verify(userRepository).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }

        @ParameterizedTest(name = "role={0} is correctly stored and returned")
        @EnumSource(UserRole.class)
        @DisplayName("should correctly persist and return every UserRole value")
        void shouldHandleEveryRole(UserRole role) {
            CreateUserRequest request = new CreateUserRequest(
                    "Test User", "test@example.com", "pass123", role
            );
            User saved = buildSavedUser("Test User", "test@example.com", "pass123", role);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            UserResponse response = userService.createUser(request);

            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getRole()).isEqualTo(role);
            assertThat(response.role()).isEqualTo(role);
        }

        @Test
        @DisplayName("should check for existing email before attempting to save")
        void shouldCheckEmailExistenceBeforeSave() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            userService.createUser(ownerRequest);

            verify(userRepository, times(1)).existsByEmail("ade@example.com");
        }

        @Test
        @DisplayName("should call repository.save exactly once when email is unique")
        void shouldCallSaveExactlyOnce() {
            User saved = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            userService.createUser(ownerRequest);

            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("should throw DuplicateEmailException when email is already registered")
        void shouldThrowDuplicateEmailExceptionWhenEmailExists() {
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(ownerRequest))
                    .isInstanceOf(DuplicateEmailException.class);
        }

        @Test
        @DisplayName("should include the duplicate email in the exception message")
        void shouldIncludeDuplicateEmailInExceptionMessage() {
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(ownerRequest))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining("ade@example.com");
        }

        @Test
        @DisplayName("should not call repository.save when email already exists")
        void shouldNotSaveUserWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(true);

            assertThrows(DuplicateEmailException.class, () -> userService.createUser(ownerRequest));

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should not call repository.save or findById when email already exists")
        void shouldNotCallFindByIdOnDuplicateEmail() {
            when(userRepository.existsByEmail("ade@example.com")).thenReturn(true);

            assertThrows(DuplicateEmailException.class, () -> userService.createUser(ownerRequest));

            verify(userRepository, never()).save(any());
            verify(userRepository, never()).findById(any());
        }
    }

    // =========================================================================
    // getUserById
    // =========================================================================

    @Nested
    @DisplayName("getUserById()")
    class GetUserByIdTests {

        private User existingUser;

        @BeforeEach
        void setUp() {
            existingUser = buildSavedUser("Chioma Obi", "chioma@example.com", "pass1234", UserRole.MANAGER);
        }

        @Test
        @DisplayName("should return a UserResponse with the correct id when user exists")
        void shouldReturnCorrectIdWhenUserExists() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            UserResponse response = userService.getUserById(FIXED_ID);

            assertThat(response.id()).isEqualTo(FIXED_ID);
        }

        @Test
        @DisplayName("should return a UserResponse with the correct name when user exists")
        void shouldReturnCorrectNameWhenUserExists() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            UserResponse response = userService.getUserById(FIXED_ID);

            assertThat(response.name()).isEqualTo("Chioma Obi");
        }

        @Test
        @DisplayName("should return a UserResponse with the correct email when user exists")
        void shouldReturnCorrectEmailWhenUserExists() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            UserResponse response = userService.getUserById(FIXED_ID);

            assertThat(response.email()).isEqualTo("chioma@example.com");
        }

        @Test
        @DisplayName("should return a UserResponse with the correct role when user exists")
        void shouldReturnCorrectRoleWhenUserExists() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            UserResponse response = userService.getUserById(FIXED_ID);

            assertThat(response.role()).isEqualTo(UserRole.MANAGER);
        }

        @Test
        @DisplayName("should return a UserResponse with active=true when user is active")
        void shouldReturnActiveTrueWhenUserIsActive() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            UserResponse response = userService.getUserById(FIXED_ID);

            assertTrue(response.active());
        }

        @Test
        @DisplayName("should return a UserResponse with active=false when the stored user is inactive")
        void shouldReturnActiveFalseWhenUserIsInactive() {
            User inactiveUser = buildSavedUser("Deactivated User", "gone@example.com", "pass", UserRole.STAFF);
            inactiveUser.setActive(false);
            UUID inactiveId = UUID.randomUUID();
            inactiveUser.setId(inactiveId);
            when(userRepository.findById(inactiveId)).thenReturn(Optional.of(inactiveUser));

            UserResponse response = userService.getUserById(inactiveId);

            assertFalse(response.active());
        }

        @Test
        @DisplayName("should return a UserResponse with the correct createdAt timestamp when user exists")
        void shouldReturnCorrectCreatedAt() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            UserResponse response = userService.getUserById(FIXED_ID);

            assertThat(response.createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("should call repository.findById with the exact UUID that was provided")
        void shouldCallFindByIdWithCorrectUuid() {
            UUID specificId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
            User user = buildSavedUser("Tunde Adeyemi", "tunde@example.com", "pass", UserRole.STAFF);
            user.setId(specificId);
            when(userRepository.findById(specificId)).thenReturn(Optional.of(user));

            userService.getUserById(specificId);

            verify(userRepository).findById(specificId);
        }

        @Test
        @DisplayName("should call repository.findById exactly once per invocation")
        void shouldCallFindByIdExactlyOnce() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            userService.getUserById(FIXED_ID);

            verify(userRepository, times(1)).findById(FIXED_ID);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when no user exists for the given id")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            UUID unknownId = UUID.randomUUID();
            when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(unknownId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should include the missing id in the UserNotFoundException message")
        void shouldIncludeMissingIdInExceptionMessage() {
            UUID unknownId = UUID.fromString("99999999-9999-9999-9999-999999999999");
            when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(unknownId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99999999-9999-9999-9999-999999999999");
        }

        @Test
        @DisplayName("should still call repository.findById even when the user does not exist")
        void shouldCallFindByIdEvenWhenUserDoesNotExist() {
            UUID unknownId = UUID.randomUUID();
            when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserById(unknownId));

            verify(userRepository).findById(unknownId);
        }

        @Test
        @DisplayName("should not call existsByEmail or save when retrieving a user by id")
        void shouldNotTouchWriteOperationsWhenGettingById() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));

            userService.getUserById(FIXED_ID);

            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any());
        }
    }

    // =========================================================================
    // getAllUsers
    // =========================================================================

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return a list containing all users")
        void shouldReturnAllUsers() {
            User u1 = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            User u2 = buildSavedUser("Chioma Obi", "chioma@example.com", "pass", UserRole.MANAGER);
            u2.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
            when(userRepository.findAll()).thenReturn(List.of(u1, u2));

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(UserResponse::email)
                    .containsExactly("ade@example.com", "chioma@example.com");
        }

        @Test
        @DisplayName("should return an empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsersExist() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should call repository.findAll exactly once")
        void shouldCallFindAllOnce() {
            when(userRepository.findAll()).thenReturn(List.of());

            userService.getAllUsers();

            verify(userRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("should map each user entity to a UserResponse")
        void shouldMapEveryUserToResponse() {
            User u1 = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            User u2 = buildSavedUser("Emeka Nwosu", "emeka@example.com", "pass", UserRole.STAFF);
            u2.setId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
            when(userRepository.findAll()).thenReturn(List.of(u1, u2));

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).extracting(UserResponse::name)
                    .containsExactly("Ade Bello", "Emeka Nwosu");
        }
    }

    // =========================================================================
    // updateUser
    // =========================================================================

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        private User existingUser;
        private UpdateUserRequest updateRequest;

        @BeforeEach
        void setUp() {
            existingUser = buildSavedUser("Ade Bello", "ade@example.com", "secret123", UserRole.OWNER);
            updateRequest = new UpdateUserRequest("Ade Updated", "ade.updated@example.com", UserRole.MANAGER);
        }

        @Test
        @DisplayName("should return a UserResponse with the updated name")
        void shouldReturnUpdatedName() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("ade.updated@example.com")).thenReturn(false);
            User afterSave = buildSavedUser("Ade Updated", "ade.updated@example.com", "secret123", UserRole.MANAGER);
            when(userRepository.save(any(User.class))).thenReturn(afterSave);

            UserResponse response = userService.updateUser(FIXED_ID, updateRequest);

            assertThat(response.name()).isEqualTo("Ade Updated");
        }

        @Test
        @DisplayName("should return a UserResponse with the updated email")
        void shouldReturnUpdatedEmail() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("ade.updated@example.com")).thenReturn(false);
            User afterSave = buildSavedUser("Ade Updated", "ade.updated@example.com", "secret123", UserRole.MANAGER);
            when(userRepository.save(any(User.class))).thenReturn(afterSave);

            UserResponse response = userService.updateUser(FIXED_ID, updateRequest);

            assertThat(response.email()).isEqualTo("ade.updated@example.com");
        }

        @Test
        @DisplayName("should return a UserResponse with the updated role")
        void shouldReturnUpdatedRole() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("ade.updated@example.com")).thenReturn(false);
            User afterSave = buildSavedUser("Ade Updated", "ade.updated@example.com", "secret123", UserRole.MANAGER);
            when(userRepository.save(any(User.class))).thenReturn(afterSave);

            UserResponse response = userService.updateUser(FIXED_ID, updateRequest);

            assertThat(response.role()).isEqualTo(UserRole.MANAGER);
        }

        @Test
        @DisplayName("should not modify the password field during update")
        void shouldNotModifyPassword() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("ade.updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.updateUser(FIXED_ID, updateRequest);

            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("secret123");
        }

        @Test
        @DisplayName("should not modify the active flag during update")
        void shouldNotModifyActiveFlag() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("ade.updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.updateUser(FIXED_ID, updateRequest);

            verify(userRepository).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }

        @Test
        @DisplayName("should allow updating to the same email without checking for duplicates")
        void shouldAllowKeepingSameEmail() {
            UpdateUserRequest sameEmailRequest = new UpdateUserRequest("Ade Bello", "ade@example.com", UserRole.OWNER);
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            UserResponse response = userService.updateUser(FIXED_ID, sameEmailRequest);

            assertThat(response.email()).isEqualTo("ade@example.com");
            verify(userRepository, never()).existsByEmail(any());
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(FIXED_ID, updateRequest))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw DuplicateEmailException when new email belongs to another user")
        void shouldThrowWhenNewEmailIsTaken() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("ade.updated@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateUser(FIXED_ID, updateRequest))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining("ade.updated@example.com");

            verify(userRepository, never()).save(any());
        }
    }

    // =========================================================================
    // activateUser
    // =========================================================================

    @Nested
    @DisplayName("activateUser()")
    class ActivateUserTests {

        @Test
        @DisplayName("should return active=true in the UserResponse after activation")
        void shouldActivateUser() {
            User inactiveUser = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            inactiveUser.setActive(false);
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(inactiveUser));
            User afterSave = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            afterSave.setActive(true);
            when(userRepository.save(any(User.class))).thenReturn(afterSave);

            UserResponse response = userService.activateUser(FIXED_ID);

            assertTrue(response.active());
        }

        @Test
        @DisplayName("should persist active=true on the entity")
        void shouldPersistActiveFlagAsTrue() {
            User inactiveUser = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            inactiveUser.setActive(false);
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(inactiveUser));
            when(userRepository.save(any(User.class))).thenReturn(inactiveUser);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.activateUser(FIXED_ID);

            verify(userRepository).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.activateUser(FIXED_ID))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should call repository.save exactly once")
        void shouldCallSaveOnce() {
            User user = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            user.setActive(false);
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.activateUser(FIXED_ID);

            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    // =========================================================================
    // deactivateUser
    // =========================================================================

    @Nested
    @DisplayName("deactivateUser()")
    class DeactivateUserTests {

        @Test
        @DisplayName("should return active=false in the UserResponse after deactivation")
        void shouldDeactivateUser() {
            User activeUser = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(activeUser));
            User afterSave = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            afterSave.setActive(false);
            when(userRepository.save(any(User.class))).thenReturn(afterSave);

            UserResponse response = userService.deactivateUser(FIXED_ID);

            assertFalse(response.active());
        }

        @Test
        @DisplayName("should persist active=false on the entity")
        void shouldPersistActiveFlagAsFalse() {
            User activeUser = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(activeUser));
            when(userRepository.save(any(User.class))).thenReturn(activeUser);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            userService.deactivateUser(FIXED_ID);

            verify(userRepository).save(captor.capture());
            assertFalse(captor.getValue().isActive());
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deactivateUser(FIXED_ID))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should call repository.save exactly once")
        void shouldCallSaveOnce() {
            User user = buildSavedUser("Ade Bello", "ade@example.com", "pass", UserRole.OWNER);
            when(userRepository.findById(FIXED_ID)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.deactivateUser(FIXED_ID);

            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    // =========================================================================
    // toResponse mapping
    // =========================================================================

    @Nested
    @DisplayName("toResponse() mapping (observed via createUser and getUserById)")
    class ToResponseMappingTests {

        @Test
        @DisplayName("should map all six UserResponse fields correctly through createUser")
        void shouldMapAllFieldsCorrectlyViaCreateUser() {
            UUID id = UUID.fromString("cafebabe-cafe-babe-cafe-babecafebabe");
            Instant createdAt = Instant.parse("2025-06-15T08:30:00Z");

            User saved = new User();
            saved.setId(id);
            saved.setName("Ngozi Adaeze");
            saved.setEmail("ngozi@example.com");
            saved.setPassword("strongPass1");
            saved.setRole(UserRole.STAFF);
            saved.setActive(true);
            saved.setCreatedAt(createdAt);

            CreateUserRequest request = new CreateUserRequest(
                    "Ngozi Adaeze", "ngozi@example.com", "strongPass1", UserRole.STAFF
            );
            when(userRepository.existsByEmail("ngozi@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(saved);

            UserResponse response = userService.createUser(request);

            assertAll("all UserResponse fields from createUser",
                    () -> assertThat(response.id()).isEqualTo(id),
                    () -> assertThat(response.name()).isEqualTo("Ngozi Adaeze"),
                    () -> assertThat(response.email()).isEqualTo("ngozi@example.com"),
                    () -> assertThat(response.role()).isEqualTo(UserRole.STAFF),
                    () -> assertTrue(response.active()),
                    () -> assertThat(response.createdAt()).isEqualTo(createdAt)
            );
        }

        @Test
        @DisplayName("should map all six UserResponse fields correctly through getUserById")
        void shouldMapAllFieldsCorrectlyViaGetUserById() {
            UUID id = UUID.fromString("deadbeef-dead-beef-dead-beefdeadbeef");
            Instant createdAt = Instant.parse("2025-12-31T23:59:59Z");

            User stored = new User();
            stored.setId(id);
            stored.setName("Emeka Nwosu");
            stored.setEmail("emeka@example.com");
            stored.setPassword("anotherPass");
            stored.setRole(UserRole.MANAGER);
            stored.setActive(true);
            stored.setCreatedAt(createdAt);

            when(userRepository.findById(id)).thenReturn(Optional.of(stored));

            UserResponse response = userService.getUserById(id);

            assertAll("all UserResponse fields from getUserById",
                    () -> assertThat(response.id()).isEqualTo(id),
                    () -> assertThat(response.name()).isEqualTo("Emeka Nwosu"),
                    () -> assertThat(response.email()).isEqualTo("emeka@example.com"),
                    () -> assertThat(response.role()).isEqualTo(UserRole.MANAGER),
                    () -> assertTrue(response.active()),
                    () -> assertThat(response.createdAt()).isEqualTo(createdAt)
            );
        }
    }
}
