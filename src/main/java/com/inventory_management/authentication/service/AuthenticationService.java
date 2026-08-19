package com.inventory_management.authentication.service;

import com.inventory_management.authentication.dto.AuthenticationResponse;
import com.inventory_management.user.dto.UserLoginRequest;

public interface AuthenticationService {

    /**
     * Authenticates a user with the provided credentials and returns a JWT access token.
     *
     * @param request the login request containing email and password
     * @return authentication response containing the JWT access token
     */
    AuthenticationResponse login(UserLoginRequest request);
}
