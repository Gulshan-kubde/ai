package com.example.ai.controller;

import com.example.ai.dto.request.LoginRequestDto;
import com.example.ai.dto.request.UserRequestDto;
import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.UserResponseDto;
import com.example.ai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@Valid @RequestBody UserRequestDto dto,
                                                                 @RequestHeader(value = "X-Request-Path", required = false) String path) {
        UserResponseDto data = userService.registerUser(dto);
        return ResponseEntity.ok(ApiResponse.success(data, "User registered successfully", 200, path != null ? path : "/api/users/register"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDto>> login(@Valid @RequestBody LoginRequestDto dto) {
        UserResponseDto data = userService.loginUser(dto);
        return ResponseEntity.ok(ApiResponse.success(data, "Login successful", 200, "/api/users/login"));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched successfully", 200, "/api/users/" + id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAll() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "All users fetched successfully", 200, "/api/users"));
    }
}
