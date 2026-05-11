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

    // Senior standard: Constructor Injection for all three dependencies
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
        // 1. Fetch user by email
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        // 2. Compare raw password with the encrypted (BCrypt) password in DB
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            
            // 3. Generate the JWT "Key"
            String token = jwtUtils.generateToken(user.getEmail());

            // 4. Return the token in a professional JSON format
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