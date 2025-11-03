package com.example.ai.service;


import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public LocalDateTime expiryTime() {
        return LocalDateTime.now().plusMinutes(5);
    }
}
