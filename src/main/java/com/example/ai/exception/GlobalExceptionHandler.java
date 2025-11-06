package com.example.ai.exception;

import com.example.ai.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest req) {
        return buildError(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRoles(InvalidRoleException ex, WebRequest req) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid Role", ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest req) {
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid Credentials", ex.getMessage(), req);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException ex) {
        throw ex; // 👈 rethrow, so Spring Security’s AccessDeniedHandler handles it (403)
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleJobNotFound(JobNotFoundException ex, WebRequest req) {
        return buildError(HttpStatus.NOT_FOUND, "Job Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(JobAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleJobAccessDenied(JobAccessDeniedException ex, WebRequest req) {
        return buildError(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req);
    }

    @ExceptionHandler(ApplicationAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicationAlreadyExists(ApplicationAlreadyExistsException ex, WebRequest req) {
        return buildError(HttpStatus.CONFLICT
                , "Already Applied", ex.getMessage(), req);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        return buildError(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest req) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", message, req);
    }



    // 🔹 Handle invalid JSON or Enum conversion (like role = USER1)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request, WebRequest req) {
        String message = "Invalid request body or value format.";

        if (ex.getCause() instanceof InvalidFormatException invalidFormat) {
            String fieldName = invalidFormat.getPath().isEmpty()
                    ? "unknown"
                    : invalidFormat.getPath().get(0).getFieldName();
            String targetType = invalidFormat.getMessage();
            message = String.format("Invalid value for field '%s'. Expected a valid %s value.", fieldName, targetType);
        }


        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", message, req);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex, WebRequest req) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", ex.getMessage(), req);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(HttpStatus status, String error, String message, WebRequest req) {
        ApiResponse<Void> response = ApiResponse.error(error, message, status.value(), req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(status).body(response);
    }
}
