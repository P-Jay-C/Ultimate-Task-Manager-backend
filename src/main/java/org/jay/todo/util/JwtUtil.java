package org.jay.todo.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jay.todo.entity.Role;
import org.jay.todo.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${auth.jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        long expirationTime = 86400000; // 24 hours in milliseconds
        return Jwts.builder()
                .subject(user.getUsername())
                .id(String.valueOf(user.getId()))
                .issuer("UltimateToDo")
                .claim("roles", user.getRoles().stream().map(Role::getName).toList())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        long refreshExpirationTime = 604800000; // 7 days in milliseconds
        return Jwts.builder()
                .subject(user.getUsername())
                .id(String.valueOf(user.getId()))
                .issuer("UltimateToDo")
                .claim("type", "refresh") // Indicate this is a refresh token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long extractUserId(String token) {
        String id = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getId();
        return id != null ? Long.valueOf(id) : null;
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", java.util.List.class);
    }

    public boolean validateToken(String token) throws ExpiredJwtException {
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new ExpiredJwtException(null, claims, "Token has expired");
        }
        return true;
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }
}