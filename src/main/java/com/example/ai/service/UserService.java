package com.example.ai.service;

import com.example.ai.dto.request.LoginRequestDto;
import com.example.ai.dto.request.UserRequestDto;
import com.example.ai.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto registerUser(UserRequestDto request);
    UserResponseDto loginUser(LoginRequestDto request);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
}
