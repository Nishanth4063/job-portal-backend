package com.nishanth.jobportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nishanth.jobportal.entity.Application;
import com.nishanth.jobportal.service.ApplicationService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    // Constructor Injection (Industry Standard over @Autowired)
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // POST /api/applications/apply/{userId}/{jobId}
    @PostMapping("/apply/{userId}/{jobId}")
    public ResponseEntity<Application> applyToJob(@PathVariable Long userId, @PathVariable Long jobId) {
        // CORRECTION: Changed from applyForJob to applyToJob to sync with your Service layer
        Application application = applicationService.applyToJob(userId, jobId);
        return new ResponseEntity<>(application, HttpStatus.CREATED); // Explicit 201 Created Status
    }

    // GET /api/applications/candidate/{userId}
    @GetMapping("/candidate/{userId}")
    public ResponseEntity<List<Application>> getApplicationsByCandidate(@PathVariable Long userId) {
        // CORRECTION: Changed from getApplicationsBySeeker to getApplicationsByCandidate
        List<Application> applications = applicationService.getApplicationsByCandidate(userId);
        return ResponseEntity.ok(applications); // 200 OK
    }

    // GET /api/applications/job/{jobId}
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable Long jobId) {
        List<Application> applications = applicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(applications); // 200 OK
    }

    // PUT /api/applications/{applicationId}/status?status=ACCEPTED&employerId=1
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            @RequestParam Long employerId) {
        
        Application updatedApplication = applicationService.updateApplicationStatus(applicationId, status, employerId);
        return ResponseEntity.ok(updatedApplication); // 200 OK
    }
}