package org.jay.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jay.todo.dto.AuthResponseDTO;
import org.jay.todo.entity.Role;
import org.jay.todo.entity.User;
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

    public AuthResponseDTO register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        log.info("Role before setting: " + userRole); // Debug
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        User savedUser = userRepository.save(user);

        return AuthResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .token(jwtUtil.generateToken(savedUser))
                .roles(savedUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    public AuthResponseDTO login(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new AuthenticationException("Invalid credentials") {};
        }

        return AuthResponseDTO.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .token(jwtUtil.generateToken(existingUser))
                .roles(existingUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}