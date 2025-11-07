package com.example.ai.service.impl;

import com.example.ai.config.JwtTokenUtil;
import com.example.ai.dto.request.LoginRequestDto;
import com.example.ai.dto.request.UserRequestDto;
import com.example.ai.dto.response.UserResponseDto;
import com.example.ai.exception.*;
import com.example.ai.model.CandidateDocument;
import com.example.ai.model.User;
import com.example.ai.repository.CandidateDocumentRepository;
import com.example.ai.repository.UserRepository;
import com.example.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.example.ai.util.Base64Utils.toBase64;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final CandidateDocumentRepository candidateDocumentRepository;

    @Override
    public UserResponseDto registerUser(UserRequestDto request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new UserAlreadyExistsException("Email is already registered: " + request.getEmail());
        });

        String raw = request.getRole();
        User.Role userRole = User.Role.USER;

        if (raw != null && !raw.trim().isEmpty()) {

            try {
                userRole =  User.Role.valueOf(raw.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
               throw new InvalidRoleException("Role must be USER or ADMIN");
            }
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNo(request.getMobileNo())
                .verified(false)
                .role(userRole)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        CandidateDocument document = candidateDocumentRepository.findByUserId(user.getId()).orElse(null);


        return UserResponseDto.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .mobileNo(savedUser.getMobileNo())
                .verified(savedUser.isVerified())
                .role(savedUser.getRole())
                .resume(document != null ? toBase64(document.getResumeData()) : null)
                .photo(document != null ? toBase64(document.getPhotoData()) : null)
                .build();
    }

    @Override
    public Map<String, Object> loginUser(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());
        CandidateDocument document = candidateDocumentRepository.findByUserId(user.getId()).orElse(null);


        UserResponseDto userDto = UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNo(user.getMobileNo())
                .verified(user.isVerified())
                .role(user.getRole())
                .resume(document != null ? toBase64(document.getResumeData()) : null)
                .photo(document != null ? toBase64(document.getPhotoData()) : null)
                .build();

        return Map.of(
                "token", token,
                "user", userDto
        );

    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        CandidateDocument document = candidateDocumentRepository.findByUserId(user.getId()).orElse(null);

        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNo(user.getMobileNo())
                .verified(user.isVerified())
                .role(user.getRole())
                .resume(document != null ? toBase64(document.getResumeData()) : null)
                .photo(document != null ? toBase64(document.getPhotoData()) : null)
                .build();
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UserResponseDto mapToResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNo(user.getMobileNo())
                .verified(user.isVerified())
                .role(user.getRole())
                .build();
    }

}
