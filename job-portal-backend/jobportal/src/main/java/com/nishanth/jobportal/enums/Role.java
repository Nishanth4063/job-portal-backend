package com.nishanth.jobportal.enums;

public enum Role {
    CANDIDATE("ROLE_CANDIDATE", "Job Seeker"),
    RECRUITER("ROLE_RECRUITER", "Employer / Recruiter"),
    ADMIN("ROLE_ADMIN", "System Administrator");

    private final String authority;
    private final String displayName;

    // Private Constructor for Enum Properties
    Role(String authority, String displayName) {
        this.authority = authority;
        this.displayName = displayName;
    }

    /**
     * Returns the exact string authority with the 'ROLE_' prefix 
     * required by Spring Security's SimpleGrantedAuthority matcher.
     */
    public String getAuthority() {
        return this.authority;
    }

    /**
     * Returns a human-readable display label for frontend UI elements.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Helper evaluation check to clean up conditional statements in your service layers.
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isRecruiter() {
        return this == RECRUITER;
    }

    public boolean isCandidate() {
        return this == CANDIDATE;
    }
}