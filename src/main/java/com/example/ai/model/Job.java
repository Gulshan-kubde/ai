package com.example.ai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;  // 👈 Linked to recruiter (User)

    @Column(nullable = false)
    private String title;

    private String description;
    private String skillsRequired;
    private String location;
    private String experienceLevel;
    private String salaryRange;

    private Boolean isActive = true;
    private LocalDateTime createdAt ;
    private LocalDateTime updatedAt;
}
