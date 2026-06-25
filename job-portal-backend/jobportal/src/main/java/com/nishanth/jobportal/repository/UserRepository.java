package com.nishanth.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nishanth.jobportal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // POLISHED: Wrapped inside Optional container to enforce runtime null-safety
    Optional<User> findByEmail(String email);
    
    // POLISHED: Efficient boolean check for registration validation workflows
    boolean existsByEmail(String email);
}