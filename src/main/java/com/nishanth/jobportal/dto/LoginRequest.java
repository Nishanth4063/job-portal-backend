package com.nishanth.jobportal.dto;

import lombok.Data;

@Data

public class LoginRequest {
    private String email;
    private String password;
}

 // 1. Generate Token: Creates the "ID Card" for the user
 // 2. Extract Data: Reads the email back from the "ID Card"
  // 3. Validation: Checks if the card is fake or expired