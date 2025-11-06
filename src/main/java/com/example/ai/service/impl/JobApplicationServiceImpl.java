package com.example.ai.service.impl;

import com.example.ai.dto.request.JobApplicationRequestDto;
import com.example.ai.dto.response.JobApplicationResponseDto;
import com.example.ai.exception.ApplicationAlreadyExistsException;
import com.example.ai.exception.JobNotFoundException;
import com.example.ai.exception.ResourceNotFoundException;
import com.example.ai.model.Job;
import com.example.ai.model.JobApplication;
import com.example.ai.model.User;
import com.example.ai.repository.JobApplicationRepository;
import com.example.ai.repository.JobRepository;
import com.example.ai.repository.UserRepository;
import com.example.ai.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    public JobApplicationResponseDto applyForJob(JobApplicationRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job not found with ID: " + request.getJobId()));

        if (jobApplicationRepository.existsByUserIdAndJobJobId(request.getUserId(), request.getJobId())) {
            throw new ApplicationAlreadyExistsException("User already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .user(user)
                .job(job)
                .status(JobApplication.ApplicationStatus.PENDING)
                .build();

        JobApplication saved = jobApplicationRepository.save(application);

        return JobApplicationResponseDto.builder()
                .applicationId(saved.getId())
                .userId(user.getId())
                .jobId(job.getJobId())
                .candidateName(user.getFullName())
                .jobTitle(job.getTitle())
                .status(saved.getStatus().toString())
                .appliedAt(saved.getAppliedAt())
                .build();
    }

    @Override
    public List<JobApplicationResponseDto> getApplicationsByUser(Long userId) {
        List<JobApplication> applications = jobApplicationRepository.findByUserId(userId);

        if (applications == null || applications.isEmpty()) {
            throw new ResourceNotFoundException("No job applications found for user ID: " + userId);
        }

        return applications.stream()
                .map(app -> JobApplicationResponseDto.builder()
                        .applicationId(app.getId())
                        .userId(app.getUser().getId())
                        .jobId(app.getJob().getJobId())
                        .candidateName(app.getUser().getFullName())
                        .jobTitle(app.getJob().getTitle())
                        .status(app.getStatus().toString())
                        .appliedAt(app.getAppliedAt())
                        .build())
                .toList();
    }


    @Override
    public List<JobApplicationResponseDto> getApplicationsByJob(Long jobId) {
        List<JobApplication> applications = jobApplicationRepository.findByJobJobId(jobId);

        if (applications == null || applications.isEmpty()) {
            throw new ResourceNotFoundException("No candidates have applied for this job yet.");
        }

        return applications.stream()
                .map(app -> JobApplicationResponseDto.builder()
                        .applicationId(app.getId())
                        .userId(app.getUser().getId())
                        .jobId(app.getJob().getJobId())
                        .candidateName(app.getUser().getFullName())
                        .jobTitle(app.getJob().getTitle())
                        .status(app.getStatus().toString())
                        .appliedAt(app.getAppliedAt())
                        .build())
                .toList();
    }

}
