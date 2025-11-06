package com.example.ai.service;


import com.example.ai.dto.request.JobRequestDto;
import com.example.ai.dto.response.JobResponseDto;

public interface JobService {
    JobResponseDto createJob(JobRequestDto request);
}
