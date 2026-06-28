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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "applications")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Application { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // POLISHED: FetchType.LAZY prevents unnecessary database joins when querying applications
    // POLISHED: Exclude relations from Lombok to completely stop StackOverflowError infinite loops
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User seeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Job job;

    @Column(nullable = false)
    private LocalDateTime appliedDate;

    // POLISHED: Enforce column definition defaults inside the SQL Server engine schema metadata
    @Column(nullable = false, length = 20)
    private String status;

    // 🎯 FIX: Added the missing resumeUrl property to map the database table update
    // Lombok's @Data will automatically generate getResumeUrl() and setResumeUrl() on compilation
    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    // POLISHED: Entity Lifecycle Hook to automatically handle defaults before persisting to database
    @PrePersist
    protected void onCreate() {
        if (this.appliedDate == null) {
            this.appliedDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PENDING"; // Enforces application consistency automatically
        }
    }
}