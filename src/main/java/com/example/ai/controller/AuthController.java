package com.example.ai.controller;



import com.example.ai.dto.response.AuthData;
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


}