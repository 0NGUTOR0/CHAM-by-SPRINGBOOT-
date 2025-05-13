package com.example.cham.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    boolean validateToken(String token);
    Authentication getAuthentication(String token);
    String generateToken(Long userId, String username);
    String resolveToken(HttpServletRequest request);
}
