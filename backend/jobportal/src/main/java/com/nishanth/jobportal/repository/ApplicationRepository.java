package com.nishanth.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nishanth.jobportal.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // POLISHED: Uses JOIN FETCH to pull the relational graph in 1 single optimized SQL query
    @Query("SELECT a FROM Application a JOIN FETCH a.seeker JOIN FETCH a.job WHERE a.seeker.id = :seekerId")
    List<Application> findBySeekerId(@Param("seekerId") Long seekerId);

    // POLISHED: Uses JOIN FETCH to pull job application queues efficiently for the recruiter dashboard
    @Query("SELECT a FROM Application a JOIN FETCH a.seeker JOIN FETCH a.job WHERE a.job.id = :jobId")
    List<Application> findByJobId(@Param("jobId") Long jobId);

    // POLISHED: Refactored to accept raw IDs instead of heavy entity objects to optimize check times
    boolean existsBySeekerIdAndJobId(Long seekerId, Long jobId);
}