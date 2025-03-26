package org.jay.todo.service;

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
        return userMapper.toAuthResponseDTO(savedUser, token);
    }

    public AuthResponseDTO login(LoginRequest request) {
        User existingUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new AuthenticationException("Invalid credentials") {};
        }
        String token = jwtUtil.generateToken(existingUser);
        return userMapper.toAuthResponseDTO(existingUser, token);
    }
}