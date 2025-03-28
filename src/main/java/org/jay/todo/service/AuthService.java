package org.jay.todo.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jay.todo.dto.AuthResponseDTO;
import org.jay.todo.dto.LoginRequest;
import org.jay.todo.dto.RegistrationRequest;
import org.jay.todo.entity.Role;
import org.jay.todo.entity.User;
import org.jay.todo.mapper.UserMapper;
import org.jay.todo.repository.RoleRepository;
import org.jay.todo.repository.UserRepository;
import org.jay.todo.util.JwtUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
        }
    }

    public AuthResponseDTO register(RegistrationRequest registrationRequest) {
        User user = userMapper.toUserEntity(registrationRequest);
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        Role role = roleRepository.findByName(registrationRequest.getRole() != null ? registrationRequest.getRole() : "USER")
                .orElseThrow(() -> new RuntimeException("Role not found: " + registrationRequest.getRole()));
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);
        return AuthResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .token(token)
                .refreshToken(refreshToken)
                .roles(savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    public AuthResponseDTO login(LoginRequest request) {
        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new AuthenticationException("Invalid credentials") {};
        }
        String token = jwtUtil.generateToken(existingUser);
        String refreshToken = jwtUtil.generateRefreshToken(existingUser);
        return AuthResponseDTO.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .token(token)
                .refreshToken(refreshToken)
                .roles(existingUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new ExpiredJwtException(null, null, "Invalid or expired refresh token");
        }
        String username = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String newToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user); // Optionally renew refresh token
        return AuthResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(newToken)
                .refreshToken(newRefreshToken)
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
}