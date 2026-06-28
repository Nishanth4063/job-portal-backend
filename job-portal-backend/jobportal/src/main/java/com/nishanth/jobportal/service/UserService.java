package com.nishanth.jobportal.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder; // 🎯 NEW IMPORT
import org.springframework.stereotype.Service;

import com.nishanth.jobportal.entity.User;
import com.nishanth.jobportal.exception.DuplicateEmailException;
import com.nishanth.jobportal.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 🎯 NEW: Injected Security Bean

    // Constructor Injection (Enforces complete initialization)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Delegates directly to registerUser for proper validation boundaries.
     */
    public User saveUser(User user) {
        return this.registerUser(user);
    }

    /**
     * Persists a new User after validating email uniqueness and hashing credentials.
     */
    public User registerUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User payload, email, and password parameters must not be null");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Registration Failure: A user with email " + user.getEmail() + " already exists.");
        }

        // 🎯 SECURITY FIX: Intercept raw password, pass through BCrypt, re-assign hashed string
        String rawPassword = user.getPassword();
        String secureHashedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(secureHashedPassword);

        return userRepository.save(user);
    }

    /**
     * INDUSTRY FIX: Throws NoSuchElementException to bind with GlobalExceptionHandler's 404 mapping.
     */
    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Target user lookup ID must not be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource Allocation Error: User not found with ID: " + id));
    }

    /**
     * INDUSTRY FIX: Throws NoSuchElementException to bind with GlobalExceptionHandler's 404 mapping.
     */
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Target lookup email query parameter must not be empty");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Authentication Failure: User profile not found for email: " + email));
    }

    /**
     * Fetches all registered system users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * INDUSTRY FIX: Throws NoSuchElementException if target user profile does not exist.
     */
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Target deletion ID must not be null");
        }
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Resource Deletion Failure: Target user profile does not exist with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}