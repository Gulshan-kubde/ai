package com.example.ai.controller;


import com.example.ai.dto.request.JobRequestDto;
import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.JobResponseDto;
import com.example.ai.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
}
