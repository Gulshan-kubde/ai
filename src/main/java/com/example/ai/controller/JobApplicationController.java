package com.example.ai.controller;

import com.example.ai.dto.request.JobApplicationRequestDto;
import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.JobApplicationResponseDto;
import com.example.ai.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<JobApplicationResponseDto>> applyForJob(
            @Valid @RequestBody JobApplicationRequestDto request) {

        JobApplicationResponseDto response = jobApplicationService.applyForJob(request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Application submitted successfully", 200, "/api/applications/apply")
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<JobApplicationResponseDto>>> getUserApplications(
            @PathVariable Long userId) {

        List<JobApplicationResponseDto> response = jobApplicationService.getApplicationsByUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Fetched all job applications for user", 200, "/api/applications/user/" + userId)
        );
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<JobApplicationResponseDto>>> getJobApplications(
            @PathVariable Long jobId) {

        List<JobApplicationResponseDto> response = jobApplicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Fetched all candidates for job", 200, "/api/applications/job/" + jobId)
        );
    }
}
