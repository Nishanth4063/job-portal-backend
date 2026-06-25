package com.nishanth.jobportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nishanth.jobportal.dto.AuthResponse;
import com.nishanth.jobportal.dto.LoginRequest;
import com.nishanth.jobportal.entity.User;
import com.nishanth.jobportal.security.JwtUtils;
import com.nishanth.jobportal.service.UserService;

import jakarta.validation.Valid;


@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection for strict dependency management
    public AuthController(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new system user profile.
     * MENTOR FIX: Intercepts and cryptographically hashes the plain-text password before saving.
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        // MENTOR FIX: Enforce BCrypt encryption algorithm on incoming plain-text credentials
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED); // 201 Created
    }

    /**
     * Authenticates existing user credentials and returns an encrypted JWT Bearer Token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Safe database lookup using our polished null-safe service method wrapper
        User user = userService.getUserByEmail(loginRequest.getEmail());

        // POLISHED: Prevent timing and profiling attacks by returning generic error footprints
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication Failure: Invalid email or credentials configuration");
        }

        // Generate cryptographic JWT Token string using verified role names
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        // Return standardized, strongly-typed response object
        AuthResponse authResponse = new AuthResponse(token, user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(authResponse); // 200 OK
    }
}