package com.nishanth.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nishanth.jobportal.entity.Application;
import com.nishanth.jobportal.entity.Job; 
import com.nishanth.jobportal.entity.User;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findBySeekerId(Long userId);

    boolean existsBySeekerAndJob(User seeker, Job job);

    List<Application> findByJobId(Long jobId);
}