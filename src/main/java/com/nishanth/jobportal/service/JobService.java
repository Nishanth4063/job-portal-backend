package com.nishanth.jobportal.service;

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
        
        // Only take the name from the User entity to protect privacy
        if (job.getPostedBy() != null) {
            dto.setPostedByName(job.getPostedBy().getName());
        }
        return dto;
    }

    // --- UPDATED FETCH ALL ---
    public List<JobResponseDTO> fetchAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- UPDATED SEARCH LOGIC ---
    public List<JobResponseDTO> searchJobs(String title, String location) {
        List<Job> jobs;
        if (title != null && !title.isEmpty() && location != null && !location.isEmpty()) {
            // Logic Fix: Use "And" for specific searches as suggested by Claude
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

    // --- EXISTING SAVE LOGIC ---
    public Job saveJob(Job job, Long userId) {
        if (userId == null) {
        throw new IllegalArgumentException("User ID must not be null");
    }

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getRole() != Role.EMPLOYER) {
            throw new RuntimeException("Access Denied: Only Employers can post jobs.");
        }

        job.setPostedBy(user);
        return jobRepository.save(job);
    }
}