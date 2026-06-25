package com.nishanth.jobportal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder // POLISHED: Enables clean object creation across your service mappers
public class JobResponseDTO {
    
    private Long id;
    private String title;
    private String description;
    private String location;
    
    // POLISHED: Replaced Double with BigDecimal for exact cryptographic/financial math precision
    private BigDecimal salary; 
    
    private LocalDateTime postedDate;
    private String postedByName;
}