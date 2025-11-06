package com.example.ai.service;


import com.example.ai.dto.request.JobRequestDto;
import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.JobResponseDto;
import com.example.ai.model.Job;

import java.util.List;

public interface JobService {
    JobResponseDto createJob(JobRequestDto request);
    List<JobResponseDto> getAllJobsLatestFirst();
    JobResponseDto getJobById(Long jobId);
    List<JobResponseDto> getJobsByRecruiterId(Long recruiterId);

}
