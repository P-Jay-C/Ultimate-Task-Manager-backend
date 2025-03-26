package org.jay.todo.mapper;

import org.jay.todo.dto.AuthResponseDTO;
import org.jay.todo.dto.RegistrationRequest;
import org.jay.todo.entity.Role;
import org.jay.todo.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toUserEntity(RegistrationRequest registrationRequest) {
        if (registrationRequest == null) {
            return null;
        }
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword()); // Password will be encoded in service
        return user;
    }

    public AuthResponseDTO toAuthResponseDTO(User user, String token) {
        if (user == null) {
            return null;
        }
        return AuthResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}