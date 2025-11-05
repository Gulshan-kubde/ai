package com.example.ai.service;

import com.example.ai.config.JwtTokenUtil;
import com.example.ai.dto.*;
import com.example.ai.exception.*;
import com.example.ai.model.User;
import com.example.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

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

        return UserResponseDto.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .mobileNo(savedUser.getMobileNo())
                .verified(savedUser.isVerified())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public UserResponseDto loginUser(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());

        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNo(user.getMobileNo())
                .verified(user.isVerified())
                .role(user.getRole())
                .token(token)
                .build();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
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
