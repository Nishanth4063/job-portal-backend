package com.nishanth.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.nishanth.jobportal") 
public class JobportalApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobportalApplication.class, args);
    }
}