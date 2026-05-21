package com.nishanth.jobportal.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

/**
 * Enterprise Polished data structure used to return standardized, 
 * immutable error details to full-stack API client applications.
 */
@Getter // POLISHED: Cleaned boilerplate code using Lombok's getter generation engine
public class ErrorResponse {

    // POLISHED: Enforces a predictable, standardized ISO-8601 string format for your Angular UI
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime timestamp;
    
    private final int status;
    private final String error; // POLISHED: Added explicit HTTP Status string name (e.g., "CONFLICT")
    private final String message;

    /**
     * Constructs a unified ErrorResponse envelope payload.
     * * @param status  the numeric HTTP status code value
     * @param error   the literal HTTP status string name representation
     * @param message the custom descriptive error text detail
     */
    public ErrorResponse(int status, String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }
}