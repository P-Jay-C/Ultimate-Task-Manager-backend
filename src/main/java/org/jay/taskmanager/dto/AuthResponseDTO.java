package org.jay.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String token;
    private String refreshToken;
    private Set<String> roles;
}