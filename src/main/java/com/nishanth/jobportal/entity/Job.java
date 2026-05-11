package com.nishanth.jobportal.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "jobs")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(length = 1000) // Professional practice: specify length for descriptions
    private String description;
    
    private String location;
    private Double salary;
    
    private LocalDateTime postedDate = LocalDateTime.now(); 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Added nullable=false for data integrity
    private User postedBy; 
}