package org.jay.todo.security;

import lombok.RequiredArgsConstructor;
import org.jay.todo.entity.Role;
import org.jay.todo.entity.User;
import org.jay.todo.repository.RoleRepository;
import org.jay.todo.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name") != null ? oAuth2User.getAttribute("name") : email.split("@")[0];

        User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(username);
                newUser.setPassword(""); // No password for OAuth users
                Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
                newUser.setRoles(new HashSet<>(Collections.singletonList(userRole)));
                return userRepository.save(newUser);
            });

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
            user.getAuthorities(),
            oAuth2User.getAttributes(),
            "email"
        );
    }
}