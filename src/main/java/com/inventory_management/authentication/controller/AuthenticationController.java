package com.inventory_management.authentication.controller;

import com.inventory_management.authentication.dto.AuthenticationResponse;
import com.inventory_management.authentication.service.AuthenticationService;
import com.inventory_management.user.dto.UserLoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
