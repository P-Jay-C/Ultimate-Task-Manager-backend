package org.jay.todo.controller;

import lombok.RequiredArgsConstructor;
import org.jay.todo.dto.AuthResponseDTO;
import org.jay.todo.entity.User;
import org.jay.todo.exception.SuccessResponse;
import org.jay.todo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@RequestBody User user) {
        AuthResponseDTO authResponse = authService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse(HttpStatus.CREATED.value(), "User registered successfully", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@RequestBody User user) {
        AuthResponseDTO authResponse = authService.login(user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Login successful", authResponse));
    }
}