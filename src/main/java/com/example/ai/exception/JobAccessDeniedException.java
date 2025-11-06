package com.example.ai.exception;

public class JobAccessDeniedException extends RuntimeException {
    public JobAccessDeniedException(String message) {
        super(message);
    }
}
