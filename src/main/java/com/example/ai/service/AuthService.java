package com.example.ai.service;



import com.example.ai.model.User;
import com.example.ai.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public String signup(String email, String name) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent() && existing.get().isVerified()) {
            return "User already verified. Please login.";
        }

        User user = existing.orElse(User.builder().email(email).fullName(name).build());
        user.setOtpCode(otpService.generateOtp());
        user.setOtpExpiry(otpService.expiryTime());
        userRepository.save(user);

        // TODO: integrate email service to send OTP
        return "OTP sent to " + email;
    }

    public String verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtpCode().equals(otp) && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
            user.setVerified(true);
            userRepository.save(user);
            return generateJwt(user);
        } else {
            throw new RuntimeException("Invalid or expired OTP");
        }
    }

    private String generateJwt(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
}