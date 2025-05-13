package com.example.cham.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    

    // Compare password with hashed value
    public boolean comparePassword(String candidatePassword) {
        return new BCryptPasswordEncoder().matches(candidatePassword, this.password);
    }

    // Limit setters to avoid unintended modifications
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password; // Will be hashed by @PrePersist or @PreUpdate
    }
}