package com.nishanth.jobportal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    // Define root directory path location for uploaded attachments
    private final Path rootLocation = Paths.get("uploads/resumes");

    public ApplicationService(ApplicationRepository applicationRepository, 
                              UserRepository userRepository, 
                              JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    /**
     * 🎯 FIXED RELATIONSHIP MAPPING
     * Persists a new job application record after writing the physical resume down onto the disk filesystem.
     */
    public Application applyToJob(Long userId, Long jobId, MultipartFile file) {
        if (userId == null || jobId == null || file == null) {
            throw new IllegalArgumentException("User ID, Job ID, and Resume file parameters must not be null");
        }

        User seeker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        if (seeker.getRole() != Role.CANDIDATE) {
            throw new RuntimeException("Access Denied: Only Job Seekers can apply for jobs.");
        }

        boolean alreadyApplied = applicationRepository.existsBySeekerIdAndJobId(userId, jobId);
        if (alreadyApplied) {
            throw new RuntimeException("Operation Failure: You have already submitted an application for this job.");
        }

        try {
            // Ensure target upload subdirectory workspace exists dynamically
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // Create an unalterable unique string name to prevent file collisions
            String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename)).normalize();

            // Stream byte streams and copy the raw file payload onto disk storage space
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Construct relative URL path for database record mapping
            String relativeResumeUrl = "/uploads/resumes/" + uniqueFilename;

            // 🎯 FIXED: Fully populated dependencies are attached to prevent Hibernate NullPointer Exceptions
            Application application = new Application();
            application.setSeeker(seeker); 
            application.setJob(job);
            application.setAppliedDate(LocalDateTime.now());
            application.setStatus("PENDING");
            application.setResumeUrl(relativeResumeUrl); 

            return applicationRepository.save(application);

        } catch (IOException e) {
            throw new RuntimeException("File System Processing Failure: Unable to securely store your resume attachment.", e);
        }
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
     * Fetches incoming job applications across ALL jobs posted by a specific recruiter.
     */
    public List<Application> getApplicationsByRecruiter(Long recruiterId) {
        if (recruiterId == null) {
            throw new IllegalArgumentException("Recruiter ID parameter must not be null");
        }
        return applicationRepository.findByRecruiterId(recruiterId);
    }

    /**
     * Updates candidate application progress records. Enforces ownership authorization rules.
     */
    public Application updateApplicationStatus(Long applicationId, String status, Long employerId) {
        if (applicationId == null || employerId == null || status == null) {
            throw new IllegalArgumentException("Application ID, Employer ID, and Status parameters must not be null");
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application record not found with ID: " + applicationId));

        if (application.getJob() == null || 
            application.getJob().getPostedBy() == null || 
            !application.getJob().getPostedBy().getId().equals(employerId)) {
            
            throw new RuntimeException("Access Denied: You are not authorized to modify this job application state.");
        }

        application.setStatus(status.toUpperCase());
        return applicationRepository.save(application);
    }
}