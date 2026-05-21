package com.nishanth.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nishanth.jobportal.entity.Job;

/**
 * Enterprise Polished Data Access Object for managing Job entity persistence.
 * Targeted fields must be backed by Full-Text Search indexes in SQL Server.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // POLISHED: Isolated targeted filter for specific text patterns inside job titles
    List<Job> findByTitleContainingIgnoreCase(String title);

    // POLISHED: Isolated targeted filter for specific text patterns inside geographic location entries
    List<Job> findByLocationContainingIgnoreCase(String location);

    // POLISHED: Strict combined multi-conditional lookup engine (Industry Standard)
    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String title, String location);
}