package com.example.ai.controller;



import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.ai.dto.AuthData;
import com.example.ai.service.AuthService;
import com.example.ai.service.TempAuthCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TempAuthCodeStore tempAuthCodeStore;

    @PostMapping("/api/auth/exchange")
    public ResponseEntity<?> exchange(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        AuthData data = tempAuthCodeStore.get(code);
        if (data == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired code"));
        }
        tempAuthCodeStore.delete(code);
        System.out.println("jwt token : "+ data.getJwt());
        return ResponseEntity.ok(Map.of(
                "token", data.getJwt(),
                "user", data.getUser()
        ));
    }

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