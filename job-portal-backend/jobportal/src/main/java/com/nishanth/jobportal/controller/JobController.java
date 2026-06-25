package com.nishanth.jobportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nishanth.jobportal.dto.JobResponseDTO;
import com.nishanth.jobportal.entity.Job;
import com.nishanth.jobportal.service.JobService;

@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    // Constructor Injection (Industry Standard)
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // POST /api/jobs/create/{userId}
    @PostMapping("/create/{userId}")
    public ResponseEntity<JobResponseDTO> postJob(@RequestBody Job job, @PathVariable Long userId) {
        JobResponseDTO savedJobDto = jobService.saveJob(job, userId);
        return new ResponseEntity<>(savedJobDto, HttpStatus.CREATED); // 201 Created
    }

    // GET /api/jobs/all
    @GetMapping("/all")
    public ResponseEntity<List<JobResponseDTO>> getAllJobs() { 
        List<JobResponseDTO> jobs = jobService.fetchAllJobs();
        return ResponseEntity.ok(jobs); // 200 OK
    }

    // GET /api/jobs/search?title=Java&location=Bengaluru
    @GetMapping("/search")
    public ResponseEntity<List<JobResponseDTO>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location) {
        
        List<JobResponseDTO> results = jobService.searchJobs(title, location); 
        return ResponseEntity.ok(results); // 200 OK
    }
}