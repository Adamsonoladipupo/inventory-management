package com.inventory_management.authentication.service;

import com.inventory_management.authentication.dto.AuthenticationResponse;
import com.inventory_management.authentication.security.JwtService;
import com.inventory_management.user.dto.UserLoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthenticationResponse login(UserLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        String token = jwtService.generateToken(authentication.getName(), role);

        return AuthenticationResponse.of(token, jwtService.getExpirationSeconds());
    }
}
