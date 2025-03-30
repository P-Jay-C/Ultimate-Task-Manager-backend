package org.jay.taskmanager.security;

import lombok.RequiredArgsConstructor;
import org.jay.taskmanager.entity.User;
import org.jay.taskmanager.repository.UserRepository;
import org.jay.taskmanager.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found after OAuth login"));

        String token = jwtUtil.generateToken(user);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Login successful\", \"token\": \"" + token + "\"}");
    }
}