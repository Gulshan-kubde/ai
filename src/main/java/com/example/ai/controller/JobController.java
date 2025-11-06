package com.example.ai.controller;


import com.example.ai.dto.request.JobRequestDto;
import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.JobResponseDto;
import com.example.ai.model.Job;
import com.example.ai.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<JobResponseDto>> createJob(
            @Valid @RequestBody JobRequestDto request) {

        JobResponseDto jobResponse = jobService.createJob(request);
        return ResponseEntity.ok(ApiResponse.success(jobResponse, "Job created successfully", 200, "/api/jobs/create"));


    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<JobResponseDto>>> getAllJobs() {
        List<JobResponseDto> response = jobService.getAllJobsLatestFirst();
        return ResponseEntity.ok(ApiResponse.success(response, "Get All Jobs successfully", 200, "/api/jobs/all"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponseDto>> getJobById(@PathVariable Long id) {
        JobResponseDto job = jobService.getJobById(id);
        return ResponseEntity.ok(
                ApiResponse.success(job, "Job fetched successfully", 200, "/api/jobs/" + id)
        );
    }

    @GetMapping("/recruiter/{recruiterId}")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<ApiResponse<List<JobResponseDto>>> getJobsByRecruiterId(@PathVariable Long recruiterId) {
        List<JobResponseDto> jobs = jobService.getJobsByRecruiterId(recruiterId);
        return ResponseEntity.ok(
                ApiResponse.success(jobs, "Jobs fetched successfully for recruiter ID: " + recruiterId, 200, "/api/jobs/recruiter/" + recruiterId)
        );
    }


}
