package com.nishanth.jobportal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               
@NoArgsConstructor   
@AllArgsConstructor  
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Double salary;
    private LocalDateTime postedDate;
    private String postedByName;
}