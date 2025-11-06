package com.example.ai.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponseDto {

    private Long jobId;
    private Long recruiterId;
    private String title;
    private String description;
    private String skillsRequired;
    private String location;
    private String experienceLevel;
    private String salaryRange;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
