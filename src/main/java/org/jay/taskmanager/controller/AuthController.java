package org.jay.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.jay.taskmanager.dto.AuthResponseDTO;
import org.jay.taskmanager.dto.LoginRequest;
import org.jay.taskmanager.dto.RefreshTokenRequest;
import org.jay.taskmanager.dto.RegistrationRequest;
import org.jay.taskmanager.exception.SuccessResponse;
import org.jay.taskmanager.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@Valid @RequestBody RegistrationRequest request) {
        AuthResponseDTO authResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse(HttpStatus.CREATED.value(), "User registered successfully", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponseDTO authResponse = authService.login(request);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Login successful", authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse> refresh(@RequestBody RefreshTokenRequest request) {
        AuthResponseDTO authResponse = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Token refreshed successfully", authResponse));
    }
}
