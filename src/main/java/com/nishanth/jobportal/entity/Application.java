package com.nishanth.jobportal.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "applications")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Application { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User seeker;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private LocalDateTime appliedDate;
    private String status;
}