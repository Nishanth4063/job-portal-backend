package com.nishanth.jobportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nishanth.jobportal.entity.Application;
import com.nishanth.jobportal.service.ApplicationService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * 🎯 REFACTORED FOR FILE UPLOAD
     * POST /api/applications/apply/{userId}/{jobId}
     * Consumes multipart/form-data to capture both IDs and the physical resume PDF file.
     */
    @PostMapping(value = "/apply/{userId}/{jobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> applyToJob(
            @PathVariable Long userId, 
            @PathVariable Long jobId,
            @RequestParam("file") MultipartFile file) {
        
        // 🛡️ Frontend Guard Check: Enforce strictly PDF records
        if (file.isEmpty() || ! "application/pdf".equals(file.getContentType())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Submission Rejected: Please attach a valid physical PDF resume document.");
        }

        try {
            Application application = applicationService.applyToJob(userId, jobId, file);
            return new ResponseEntity<>(application, HttpStatus.CREATED); 
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File Processing Failure: " + e.getMessage());
        }
    }

    // GET /api/applications/candidate/{userId}
    @GetMapping("/candidate/{userId}")
    public ResponseEntity<List<Application>> getApplicationsByCandidate(@PathVariable Long userId) {
        List<Application> applications = applicationService.getApplicationsByCandidate(userId);
        return ResponseEntity.ok(applications); 
    }

    // GET /api/applications/job/{jobId}
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable Long jobId) {
        List<Application> applications = applicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(applications); 
    }

    // 🎯 NEW MULTI-TENANCY ENDPOINT: GET /api/applications/recruiter/{recruiterId}
    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<Application>> getApplicationsByRecruiter(@PathVariable Long recruiterId) {
        List<Application> applications = applicationService.getApplicationsByRecruiter(recruiterId);
        return ResponseEntity.ok(applications); 
    }

    // PUT /api/applications/{applicationId}/status?status=ACCEPTED&employerId=1
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            @RequestParam Long employerId) {
        
        Application updatedApplication = applicationService.updateApplicationStatus(applicationId, status, employerId);
        return ResponseEntity.ok(updatedApplication); 
    }
}