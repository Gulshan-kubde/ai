package com.example.ai.controller;



import com.example.ai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/api/auth/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email"),
                "provider", principal.getAttributes()
        );
    }

    @PostMapping("/signup")
    public String signup(@RequestBody Map<String, String> req) {
        return authService.signup(req.get("email"), req.get("name"));
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody Map<String, String> req) {
        return authService.verifyOtp(req.get("email"), req.get("otp"));
    }

    @GetMapping("/login/success")
    public String success(@RequestParam String token) {
        return "Login successful! Token: " + token;
    }
}