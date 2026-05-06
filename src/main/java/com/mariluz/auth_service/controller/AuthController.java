package com.mariluz.auth_service.controller;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.LoginRequest;
import com.mariluz.auth_service.dto.RegisterRequest;
import com.mariluz.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // inyeccion del service
    @Autowired
    private AuthService service;

    //ENDPOINTS:

    // Register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @Valid @RequestBody RegisterRequest request,
        BindingResult result
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            service.register(request)
        );
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(service.login(request));
    }
}
