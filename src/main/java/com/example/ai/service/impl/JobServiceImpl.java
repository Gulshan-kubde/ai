package com.example.ai.service.impl;


import com.example.ai.dto.request.JobRequestDto;
import com.example.ai.dto.response.JobResponseDto;
import com.example.ai.exception.ResourceNotFoundException;
import com.example.ai.model.Job;
import com.example.ai.model.User;
import com.example.ai.repository.JobRepository;
import com.example.ai.repository.UserRepository;
import com.example.ai.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public JobResponseDto createJob(JobRequestDto request) {
        User recruiter = userRepository.findById(request.getRecruiterId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + request.getRecruiterId()));

        if (!recruiter.getRole().name().equals("ADMIN") && !recruiter.getRole().name().equals("USER")) {
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
}
