package com.nishanth.jobportal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nishanth.jobportal.dto.JobResponseDTO;
import com.nishanth.jobportal.entity.Job;
import com.nishanth.jobportal.entity.User;
import com.nishanth.jobportal.enums.Role;
import com.nishanth.jobportal.repository.JobRepository;
import com.nishanth.jobportal.repository.UserRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // --- CONVERSION METHOD (ENTITY -> DTO) ---
    private JobResponseDTO mapToDTO(Job job) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setPostedDate(job.getPostedDate());
        
        if (job.getPostedBy() != null) {
            dto.setPostedByName(job.getPostedBy().getName());
        }
        return dto;
    }

    public List<JobResponseDTO> fetchAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<JobResponseDTO> searchJobs(String title, String location) {
        List<Job> jobs;
        if (title != null && !title.isEmpty() && location != null && !location.isEmpty()) {
            jobs = jobRepository.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(title, location);
        } else if (title != null && !title.isEmpty()) {
            jobs = jobRepository.findByTitleContainingIgnoreCase(title);
        } else if (location != null && !location.isEmpty()) {
            jobs = jobRepository.findByLocationContainingIgnoreCase(location);
        } else {
            jobs = jobRepository.findAll();
        }

        return jobs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- CRITICAL FIX IN SAVE LOGIC ---
    public Job saveJob(Job job, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // MENTOR FIX: Changed Role.EMPLOYER to Role.RECRUITER to match your DB/Enum
        if (user.getRole() != Role.RECRUITER) {
            throw new RuntimeException("Access Denied: Only Recruiters can post jobs.");
        }

        // Set the relationship
        job.setPostedBy(user);
        
        // Ensure standard fields are set
        if (job.getPostedDate() == null) {
            job.setPostedDate(LocalDateTime.now());
        }
        
        // Sync the name field if it exists in your Job entity
        job.setPostedByName(user.getName());

        return jobRepository.save(job);
    }
}