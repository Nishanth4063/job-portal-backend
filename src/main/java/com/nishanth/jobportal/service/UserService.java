package com.nishanth.jobportal.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nishanth.jobportal.entity.User;
import com.nishanth.jobportal.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ Constructor Injection: Industry standard for Bengaluru-based tech hubs.
     * Marks dependencies as final to ensure immutability and testability.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with BCrypt password encryption.
     */
    public User saveUser(User user) {
        // 1. Verify if the email is already in use
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        
        // 2. 🛡️ Encrypt the raw password before it reaches SQL Server
        // This ensures the database never stores plain-text credentials.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    /**
     * Retrieves all registered users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Fetches a single user by ID with strict null-safety checks.
     */
    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Deletes a user after verifying existence in the database.
     */
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}