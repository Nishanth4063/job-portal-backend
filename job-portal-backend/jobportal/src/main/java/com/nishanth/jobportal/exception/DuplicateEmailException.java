package com.nishanth.jobportal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Enterprise Polished Exception thrown when a user registration 
 * request contains an email address already registered in the system database.
 * Maps natively to HTTP Status 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DuplicateEmailException with a detailed contextual error message.
     * @param message the descriptive error detail payload
     */
    public DuplicateEmailException(String message) { // MENTOR FIX: Cleaned class keyword token
        super(message);
    }
}