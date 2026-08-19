package com.inventory_management.authentication.service;

import com.inventory_management.authentication.dto.AuthenticationResponse;
import com.inventory_management.authentication.security.JwtService;
import com.inventory_management.user.dto.UserLoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl")
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "password123";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.test.signature";
    private static final long EXPIRY_SECONDS = 3600L;

    private Authentication buildAuthentication(String email, String role) {
        return new UsernamePasswordAuthenticationToken(
                email, null,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    @Nested
    @DisplayName("login() — success")
    class LoginSuccessTests {

        @BeforeEach
        void setUp() {
            when(jwtService.generateToken(anyString(), anyString())).thenReturn(TOKEN);
            when(jwtService.getExpirationSeconds()).thenReturn(EXPIRY_SECONDS);
        }

        @Test
        @DisplayName("should return an AuthenticationResponse when credentials are valid")
        void shouldReturnAuthenticationResponseOnSuccess() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_OWNER");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            AuthenticationResponse response = authenticationService.login(
                    new UserLoginRequest(EMAIL, PASSWORD));

            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("should return the JWT access token in the response")
        void shouldReturnJwtToken() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_OWNER");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            AuthenticationResponse response = authenticationService.login(
                    new UserLoginRequest(EMAIL, PASSWORD));

            assertThat(response.accessToken()).isEqualTo(TOKEN);
        }

        @Test
        @DisplayName("should return tokenType 'Bearer' in the response")
        void shouldReturnBearerTokenType() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_OWNER");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            AuthenticationResponse response = authenticationService.login(
                    new UserLoginRequest(EMAIL, PASSWORD));

            assertThat(response.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("should return the correct expiresIn value from JwtService")
        void shouldReturnCorrectExpiresIn() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_OWNER");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            AuthenticationResponse response = authenticationService.login(
                    new UserLoginRequest(EMAIL, PASSWORD));

            assertThat(response.expiresIn()).isEqualTo(EXPIRY_SECONDS);
        }

        @Test
        @DisplayName("should pass the user's email as the JWT subject")
        void shouldPassEmailAsJwtSubject() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_MANAGER");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            authenticationService.login(new UserLoginRequest(EMAIL, PASSWORD));

            verify(jwtService).generateToken(eq(EMAIL), anyString());
        }

        @Test
        @DisplayName("should pass the user's role authority to the JWT generator")
        void shouldPassRoleToJwtGenerator() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_OWNER");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            authenticationService.login(new UserLoginRequest(EMAIL, PASSWORD));

            verify(jwtService).generateToken(anyString(), eq("ROLE_OWNER"));
        }

        @Test
        @DisplayName("should call AuthenticationManager with the correct credentials")
        void shouldCallAuthenticationManagerWithCorrectCredentials() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_STAFF");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            authenticationService.login(new UserLoginRequest(EMAIL, PASSWORD));

            verify(authenticationManager).authenticate(
                    argThat(a -> a instanceof UsernamePasswordAuthenticationToken
                            && EMAIL.equals(a.getPrincipal())
                            && PASSWORD.equals(a.getCredentials()))
            );
        }

        @Test
        @DisplayName("should call JwtService.generateToken exactly once on success")
        void shouldCallGenerateTokenOnce() {
            Authentication auth = buildAuthentication(EMAIL, "ROLE_OWNER");
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            authenticationService.login(new UserLoginRequest(EMAIL, PASSWORD));

            verify(jwtService, times(1)).generateToken(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("login() — failure")
    class LoginFailureTests {

        @Test
        @DisplayName("should throw BadCredentialsException when password is incorrect")
        void shouldThrowWhenPasswordIsIncorrect() {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authenticationService.login(
                    new UserLoginRequest(EMAIL, "wrongpassword")))
                    .isInstanceOf(BadCredentialsException.class);

            verify(jwtService, never()).generateToken(any(), any());
        }

        @Test
        @DisplayName("should throw BadCredentialsException when email does not exist")
        void shouldThrowWhenEmailDoesNotExist() {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authenticationService.login(
                    new UserLoginRequest("nobody@example.com", PASSWORD)))
                    .isInstanceOf(BadCredentialsException.class);

            verify(jwtService, never()).generateToken(any(), any());
        }

        @Test
        @DisplayName("should throw DisabledException when user account is inactive")
        void shouldThrowWhenUserIsInactive() {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new DisabledException("User account is disabled"));

            assertThatThrownBy(() -> authenticationService.login(
                    new UserLoginRequest(EMAIL, PASSWORD)))
                    .isInstanceOf(DisabledException.class);

            verify(jwtService, never()).generateToken(any(), any());
        }

        @Test
        @DisplayName("should not call JwtService when authentication fails")
        void shouldNotGenerateTokenOnFailure() {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            try {
                authenticationService.login(new UserLoginRequest(EMAIL, PASSWORD));
            } catch (BadCredentialsException ignored) {
            }

            verify(jwtService, never()).generateToken(any(), any());
        }
    }
}
