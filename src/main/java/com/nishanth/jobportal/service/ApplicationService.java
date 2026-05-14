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

    public ApplicationService(ApplicationRepository applicationRepository, 
                              UserRepository userRepository, 
                              JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Application applyForJob(Long userId, Long jobId) {
        // 1. Validation
        if (userId == null || jobId == null) {
            throw new IllegalArgumentException("User ID and Job ID must not be null");
        }

        // 2. Fetch User
        User seeker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 3. Fetch Job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        // 4. Business Rules
        // MENTOR FIX: Matches your Role.JOB_SEEKER
        if (seeker.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Access Denied: Only Job Seekers can apply for jobs.");
        }

        // MENTOR FIX: Matches your Repository existsBySeekerAndJob
        if (applicationRepository.existsBySeekerAndJob(seeker, job)) {
            throw new RuntimeException("You have already applied for this job.");
        }

        // 5. Save Application
        Application application = new Application();
        application.setSeeker(seeker); // Matches your Entity field 'seeker'
        application.setJob(job);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus("PENDING");

        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsBySeeker(Long userId) {
        // MENTOR FIX: Matches your Repository findBySeekerId
        return applicationRepository.findBySeekerId(userId);
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public Application updateApplicationStatus(Long applicationId, String status, Long employerId) {
        // 1. Always validate inputs first
        if (applicationId == null || employerId == null) {
            throw new IllegalArgumentException("Application ID and Employer ID must not be null");
        }

        // 2. FETCH THE DATA FIRST
        // This line defines the 'application' variable so the computer knows what it is.
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        // 3. SECURITY CHECK
        // Now 'application' is resolved, so these lines won't show errors.
        if (application.getJob() == null || 
            application.getJob().getPostedBy() == null || 
            !application.getJob().getPostedBy().getId().equals(employerId)) {
            
            throw new RuntimeException("Access Denied: You are not authorized to update this status.");
        }

        // 4. UPDATE AND SAVE
        application.setStatus(status.toUpperCase());
        return applicationRepository.save(application);
    }
}