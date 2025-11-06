package com.example.ai.service;

import com.example.ai.dto.request.JobApplicationRequestDto;
import com.example.ai.dto.response.JobApplicationResponseDto;

import java.util.List;

public interface JobApplicationService {
    JobApplicationResponseDto applyForJob(JobApplicationRequestDto request);
    List<JobApplicationResponseDto> getApplicationsByUser(Long userId);
    List<JobApplicationResponseDto> getApplicationsByJob(Long jobId);
}
