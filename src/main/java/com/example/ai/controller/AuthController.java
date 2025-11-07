package com.example.ai.controller;

import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.AuthData;
import com.example.ai.dto.response.UserResponseDto;
import com.example.ai.model.CandidateDocument;
import com.example.ai.model.User;
import com.example.ai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Exchange temporary auth code (from OAuth or UI) for JWT token and user details.
     */
    @PostMapping("/exchange")
    public ResponseEntity<ApiResponse<Map<String, Object>>> exchange(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing auth code", String.valueOf(HttpStatus.BAD_REQUEST.value()),400, "/api/auth/exchange"));
        }

        ApiResponse<Map<String, Object>> response = authService.exchangeAuthCode(code);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Retrieve OAuth2 user info from active session.
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active user session"));
        }
        return ResponseEntity.ok(Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email"),
                "provider", principal.getAttributes()
        ));
    }
}
