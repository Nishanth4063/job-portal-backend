package com.nishanth.jobportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nishanth.jobportal.entity.Application;
import com.nishanth.jobportal.service.ApplicationService;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    // Constructor Injection (Industry Standard)
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Endpoint for a Job Seeker to apply for a Job
    @PostMapping("/apply/{userId}/{jobId}")
    public ResponseEntity<Application> applyForJob(@PathVariable Long userId, @PathVariable Long jobId) {
        Application application = applicationService.applyForJob(userId, jobId);
        return new ResponseEntity<>(application, HttpStatus.CREATED); // 201 Created
    }

    // Endpoint to view applications by a specific Job Seeker
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> getApplicationsBySeeker(@PathVariable Long userId) {
        List<Application> applications = applicationService.getApplicationsBySeeker(userId);
        return ResponseEntity.ok(applications); // 200 OK
    }

    // GET /api/applications/job/1 -> See everyone who applied for Job 1
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId));
    }

    // DAY 6 NEW: Update Application Status (Accept/Reject)
    // Usage: PUT /api/applications/1/status?status=ACCEPTED&employerId=1
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            @RequestParam Long employerId) {
        
        Application updated = applicationService.updateApplicationStatus(applicationId, status, employerId);
        return ResponseEntity.ok(updated);
    }
}