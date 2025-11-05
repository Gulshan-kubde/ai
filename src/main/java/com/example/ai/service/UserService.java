package com.example.ai.service;

import com.example.ai.dto.*;

import java.util.List;

public interface UserService {
    UserResponseDto registerUser(UserRequestDto request);
    UserResponseDto loginUser(LoginRequestDto request);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
}
