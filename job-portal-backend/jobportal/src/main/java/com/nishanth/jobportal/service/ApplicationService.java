package com.nishanth.jobportal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nishanth.jobportal.entity.Application;
import com.nishanth.jobportal.entity.Job;
import com.nishanth.jobportal.entity.User;
import com.nishanth.jobportal.enums.Role;
import com.nishanth.jobportal.repository.ApplicationRepository;
import com.nishanth.jobportal.repository.JobRepository;
import com.nishanth.jobportal.repository.UserRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    // Constructor Injection (Industry Standard over @Autowired)
    public ApplicationService(ApplicationRepository applicationRepository, 
                              UserRepository userRepository, 
                              JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    /**
     * Persists a new job application record after evaluating candidate roles and duplicate submissions.
     */
    public Application applyToJob(Long userId, Long jobId) { // Sync name with your Controller
        // 1. Technical Input Validation
        if (userId == null || jobId == null) {
            throw new IllegalArgumentException("User ID and Job ID parameters must not be null");
        }

        // 2. Fetch Domain Entities
        User seeker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        // 3. Role-Based Access Control (RBAC) Validation
        if (seeker.getRole() != Role.CANDIDATE) {
            throw new RuntimeException("Access Denied: Only Job Seekers can apply for jobs.");
        }

        // POLISHED FIX: Invokes optimized primitive-key lookup to completely bypass entity mapping errors
        boolean alreadyApplied = applicationRepository.existsBySeekerIdAndJobId(userId, jobId);
        if (alreadyApplied) {
            throw new RuntimeException("Operation Failure: You have already submitted an application for this job.");
        }

        // 4. Assemble and Save Bridge Record
        Application application = new Application();
        application.setSeeker(seeker); 
        application.setJob(job);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus("PENDING");

        return applicationRepository.save(application);
    }

    /**
     * Fetches application history for a specific job candidate.
     */
    public List<Application> getApplicationsByCandidate(Long userId) {
        return applicationRepository.findBySeekerId(userId);
    }

    /**
     * Fetches incoming job application queues for a specific job listing.
     */
    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    /**
     * Updates candidate application progress records. Enforces ownership authorization rules.
     */
    public Application updateApplicationStatus(Long applicationId, String status, Long employerId) {
        // 1. Parameter Validation
        if (applicationId == null || employerId == null || status == null) {
            throw new IllegalArgumentException("Application ID, Employer ID, and Status parameters must not be null");
        }

        // 2. Fetch Core Entity Target
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application record not found with ID: " + applicationId));

        // 3. Security Boundary Verification
        if (application.getJob() == null || 
            application.getJob().getPostedBy() == null || 
            !application.getJob().getPostedBy().getId().equals(employerId)) {
            
            throw new RuntimeException("Access Denied: You are not authorized to modify this job application state.");
        }

        // 4. Update and Persist Data Layer Row Changes
        application.setStatus(status.toUpperCase());
        return applicationRepository.save(application);
    }
}