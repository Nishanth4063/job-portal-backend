package com.nishanth.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nishanth.jobportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}