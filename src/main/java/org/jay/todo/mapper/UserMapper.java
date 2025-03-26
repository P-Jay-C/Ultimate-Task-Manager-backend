package org.jay.todo.mapper;

import org.jay.todo.dto.AuthResponseDTO;
import org.jay.todo.dto.PagedUserResponseDTO;
import org.jay.todo.dto.RegistrationRequest;
import org.jay.todo.dto.UserDTO;
import org.jay.todo.entity.Role;
import org.jay.todo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
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
        user.setPassword(registrationRequest.getPassword());
        return user;
    }

    public User toUserEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
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

    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    public PagedUserResponseDTO toPagedUserResponseDTO(Page<User> userPage) {
        if(userPage == null) {
            return null;
        }

        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());

        return PagedUserResponseDTO.builder()
                .content(userDTOs)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
    }
}