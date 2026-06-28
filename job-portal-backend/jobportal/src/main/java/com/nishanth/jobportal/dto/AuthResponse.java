package com.nishanth.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;  // Position 1
    private String email;  // Position 2
    private String role;   // Position 3
    private Long id;       // position 4
}