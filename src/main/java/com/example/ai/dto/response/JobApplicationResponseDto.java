package com.example.ai.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobApplicationResponseDto {
    private Long applicationId;
    private Long userId;
    private Long jobId;
    private String candidateName;
    private String jobTitle;
    private String status;
    private LocalDateTime appliedAt;
}
