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

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin("*") 
public class JobController {

    private final JobService jobService;

    // Constructor Injection (Industry Standard over @Autowired)
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // POST /api/jobs/create/{userId}
    @PostMapping("/create/{userId}")
    public ResponseEntity<Job> postJob(@RequestBody Job job, @PathVariable Long userId) {
        Job savedJob = jobService.saveJob(job, userId);
        return new ResponseEntity<>(savedJob, HttpStatus.CREATED);
    }

    // GET /api/jobs/all
    @GetMapping("/all")
    public ResponseEntity<List<JobResponseDTO>> getAllJobs() { 
        return ResponseEntity.ok(jobService.fetchAllJobs());
}
    // --- DAY 5: SEARCH ENDPOINT ---
    // Usage: /api/jobs/search?title=Java&location=Bengaluru
    @GetMapping("/search")
    public ResponseEntity<List<JobResponseDTO>> searchJobs(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String location) {
    List<JobResponseDTO> results = jobService.searchJobs(title, location); // Change type here
    return ResponseEntity.ok(results);
}
}