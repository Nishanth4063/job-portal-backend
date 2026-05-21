package com.nishanth.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nishanth.jobportal.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name field is mandatory")
    @Column(nullable = false, length = 100)
    private String name;

    // POLISHED: Enforced explicit uniqueness and structured indexing rules at the SQL schema level
    @Email(message = "Please provide a structurally valid email address")
    @NotBlank(message = "Email field is mandatory")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password must not be blank")
    @Column(nullable = false, length = 255) // Length 255 is optimal for storing BCrypt hashes safely
    private String password;

    // POLISHED: Constrained column string length to optimize SQL storage indexes
    @NotNull(message = "User role assignment is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    // INDUSTRY STANDARD: Cascade deletion configurations to clear relational orphans automatically
    @com.fasterxml.jackson.annotation.JsonIgnore
    @jakarta.persistence.OneToMany(mappedBy = "seeker", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Application> applications;
}