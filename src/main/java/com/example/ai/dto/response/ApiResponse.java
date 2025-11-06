package com.example.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ✅ Universal API Response Wrapper for all endpoints (success + error)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {


    private int status;
    private String message;
    private T data;
    private boolean success;
    private LocalDateTime timestamp;
    private String error;
    private String path;



    // Factory methods for clean controller usage
    public static <T> ApiResponse<T> success(T data, String message, int status, String path) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .success(true)
                .message(message)
                .path(path)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String error, String message, int status, String path) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .success(false)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}
