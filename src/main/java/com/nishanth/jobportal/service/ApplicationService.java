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

    // Constructor Injection for all three required repositories
    public ApplicationService(ApplicationRepository applicationRepository, 
                              UserRepository userRepository, 
                              JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Application applyForJob(Long userId, Long jobId) {
        // 1. Validation: Ensure IDs are not null (Standard Safety)
        if (userId == null || jobId == null) {
            throw new IllegalArgumentException("User ID and Job ID must not be null");
        }

        // 2. Fetch User: Ensure the applicant exists
        User seeker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 3. Fetch Job: Ensure the job listing exists
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        // 4. Business Rule: Only JOB_SEEKER can apply
        if (seeker.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Access Denied: Only Job Seekers can apply for jobs.");
        }

        if (applicationRepository.existsBySeekerAndJob(seeker, job)) {
            throw new RuntimeException("You have already applied for this job.");
        }

        // 5. Create the Bridge (Associate Entity)
        Application application = new Application();
        application.setSeeker(seeker);
        application.setJob(job);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus("PENDING");

        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsBySeeker(Long userId) {
        return applicationRepository.findBySeekerId(userId);
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

public Application updateApplicationStatus(Long applicationId, String status, Long employerId) {


    // 1. Immediate Null Check (Clears the yellow underline)
    if (applicationId == null || employerId == null) {
        throw new IllegalArgumentException("Application ID and Employer ID must not be null");
    }

    // 2. Fetch the application
    Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

    // 3. Security Check: Only the Employer who posted this specific job can change the status
    // Get the User ID of the job poster and compare it with the current employerId
    if (!application.getJob().getPostedBy().getId().equals(employerId)) {
        throw new RuntimeException("Access Denied: You are not authorized to update this application.");
    }

    // 4. Update status and save
    application.setStatus(status.toUpperCase());
    return applicationRepository.save(application);
}
}