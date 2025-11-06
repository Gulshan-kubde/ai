package com.example.ai.service.impl;


import com.example.ai.dto.request.JobRequestDto;
import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.JobResponseDto;
import com.example.ai.exception.JobAccessDeniedException;
import com.example.ai.exception.JobNotFoundException;
import com.example.ai.exception.ResourceNotFoundException;
import com.example.ai.model.Job;
import com.example.ai.model.User;
import com.example.ai.repository.JobRepository;
import com.example.ai.repository.UserRepository;
import com.example.ai.service.JobService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public JobResponseDto createJob(JobRequestDto request) {
        User recruiter = userRepository.findById(request.getRecruiterId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + request.getRecruiterId()));

        if (!recruiter.getRole().name().equals("ADMIN") && !recruiter.getRole().name().equals("RECRUITER")) {
            throw new IllegalStateException("Only recruiters or admins can post jobs");
        }

        Job job = Job.builder()
                .recruiter(recruiter)
                .title(request.getTitle())
                .description(request.getDescription())
                .skillsRequired(request.getSkillsRequired())
                .location(request.getLocation())
                .experienceLevel(request.getExperienceLevel())
                .salaryRange(request.getSalaryRange())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Job savedJob = jobRepository.save(job);

        return JobResponseDto.builder()
                .jobId(savedJob.getJobId())
                .recruiterId(savedJob.getRecruiter().getId())
                .title(savedJob.getTitle())
                .description(savedJob.getDescription())
                .skillsRequired(savedJob.getSkillsRequired())
                .location(savedJob.getLocation())
                .experienceLevel(savedJob.getExperienceLevel())
                .salaryRange(savedJob.getSalaryRange())
                .isActive(savedJob.getIsActive())
                .createdAt(savedJob.getCreatedAt())
                .build();
    }

    @Override
    public List<JobResponseDto> getAllJobsLatestFirst() {
        try {
            // Fetch jobs ordered by creation date (latest first)
            List<Job> jobs = jobRepository.findAllByOrderByCreatedAtDesc();

            // Handle case where no jobs exist
            if (jobs.isEmpty()) {
                throw new JobNotFoundException("No jobs found in the system");
            }

            return jobs.stream()
                    .map(job -> JobResponseDto.builder()
                            .jobId(job.getJobId())
                            .recruiterId(job.getRecruiter().getId())
                            .title(job.getTitle())
                            .description(job.getDescription())
                            .skillsRequired(job.getSkillsRequired())
                            .location(job.getLocation())
                            .experienceLevel(job.getExperienceLevel())
                            .salaryRange(job.getSalaryRange())
                            .isActive(job.getIsActive())
                            .createdAt(job.getCreatedAt())
                            .updatedAt(job.getUpdatedAt())
                            .build())
                    .toList();


        } catch (JobNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unexpected error while fetching jobs: " + ex.getMessage(), ex);
        }
    }

    @Override
    public JobResponseDto getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with ID: " + jobId));

        return JobResponseDto.builder()
                .jobId(job.getJobId())
                .recruiterId(job.getRecruiter().getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .skillsRequired(job.getSkillsRequired())
                .location(job.getLocation())
                .experienceLevel(job.getExperienceLevel())
                .salaryRange(job.getSalaryRange())
                .isActive(job.getIsActive())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    @Override
    public List<JobResponseDto> getJobsByRecruiterId(Long recruiterId) {
        List<Job> jobs = jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId);

        if (jobs.isEmpty()) {
            throw new JobNotFoundException("No jobs found for recruiter ID: " + recruiterId);
        }

        return jobs.stream()
                .map(job -> JobResponseDto.builder()
                        .jobId(job.getJobId())
                        .recruiterId(job.getRecruiter().getId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .skillsRequired(job.getSkillsRequired())
                        .location(job.getLocation())
                        .experienceLevel(job.getExperienceLevel())
                        .salaryRange(job.getSalaryRange())
                        .isActive(job.getIsActive())
                        .createdAt(job.getCreatedAt())
                        .updatedAt(job.getUpdatedAt())
                        .build())
                .toList();
    }
    @Override
    public JobResponseDto updateJob(Long jobId, Long recruiterId, JobRequestDto requestDto) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with ID: " + jobId));

        // ✅ Check ownership — only creator or admin can update
        Long creatorId = job.getRecruiter().getId();
        boolean isAdmin = userRepository.findById(recruiterId)
                .map(u -> u.getRole() == User.Role.ADMIN)
                .orElse(false);

        if (!creatorId.equals(recruiterId) && !isAdmin) {
            throw new JobAccessDeniedException("You are not authorized to update this job.");
        }

        // ✅ Update allowed fields only
        job.setTitle(requestDto.getTitle());
        job.setDescription(requestDto.getDescription());
        job.setSkillsRequired(requestDto.getSkillsRequired());
        job.setLocation(requestDto.getLocation());
        job.setExperienceLevel(requestDto.getExperienceLevel());
        job.setSalaryRange(requestDto.getSalaryRange());
        job.setIsActive(requestDto.getIsActive());

        Job updated = jobRepository.save(job);

        return JobResponseDto.builder()
                .jobId(updated.getJobId())
                .recruiterId(updated.getRecruiter().getId())
                .title(updated.getTitle())
                .description(updated.getDescription())
                .skillsRequired(updated.getSkillsRequired())
                .location(updated.getLocation())
                .experienceLevel(updated.getExperienceLevel())
                .salaryRange(updated.getSalaryRange())
                .isActive(updated.getIsActive())
                .createdAt(updated.getCreatedAt())
                .build();
    }


}
