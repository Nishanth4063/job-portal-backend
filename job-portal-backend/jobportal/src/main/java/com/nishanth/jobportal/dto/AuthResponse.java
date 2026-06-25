package com.nishanth.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    // MENTOR FIX: Reordered fields to precisely line up with your AuthController constructor instantiation parameters
    private String token;  // Position 1
    private String email;  // Position 2
    private String role;   // Position 3
}