package com.preptracker.controller;

import com.preptracker.dto.AuthResponse;
import com.preptracker.dto.LoginRequest;
import com.preptracker.dto.RegisterRequest;
import com.preptracker.dto.UserResponse;
import com.preptracker.security.SecurityUtils;
import com.preptracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SecurityUtils securityUtils;

    public AuthController(AuthService authService, SecurityUtils securityUtils) {
        this.authService = authService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(UserResponse.from(securityUtils.getCurrentUser()));
    }

    @GetMapping("/google-url")
    public ResponseEntity<Map<String, String>> googleAuthUrl() {
        Map<String, String> body = new HashMap<>();
        body.put("url", "/oauth2/authorization/google");
        return ResponseEntity.ok(body);
    }
}
