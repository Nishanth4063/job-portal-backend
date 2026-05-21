package com.nishanth.jobportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nishanth.jobportal.dto.LoginRequest;
import com.nishanth.jobportal.entity.User;
import com.nishanth.jobportal.security.JwtUtils;
import com.nishanth.jobportal.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // IMPORTANT: Ensure this method name matches your UserService.java exactly!
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            
            // Matches your new JwtUtils.generateToken(String, String)
            String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

            // Using HashMap avoids "AuthResponse cannot be resolved" errors
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}