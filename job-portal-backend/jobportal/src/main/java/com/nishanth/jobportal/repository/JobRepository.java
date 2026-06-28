package com.nishanth.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nishanth.jobportal.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String title, String location);

    // 🎯 NEW: Fetch all jobs published exclusively by a specific recruiter profile ID
    List<Job> findByPostedById(Long recruiterId);
}