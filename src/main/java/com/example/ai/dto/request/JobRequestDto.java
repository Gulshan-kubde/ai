package com.example.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequestDto {

    @NotNull(message = "Recruiter ID is required")
    private Long recruiterId;

    @NotBlank(message = "Job title is required")
    private String title;

    private String description;
    private String skillsRequired;
    private String location;
    private String experienceLevel;
    private String salaryRange;
}
