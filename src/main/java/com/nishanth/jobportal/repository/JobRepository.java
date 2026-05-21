package com.nishanth.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nishanth.jobportal.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // 1. Search by BOTH Title and Location (Existing Day 3)
    List<Job> findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(String title, String location);

    // 2. NEW: Search specifically by Title
    List<Job> findByTitleContainingIgnoreCase(String title);

    // 3. NEW: Search specifically by Location
    List<Job> findByLocationContainingIgnoreCase(String location);

    // 4. NEW: Advanced Search - Must match BOTH Title AND Location (Industry Standard)
    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String title, String location);
}