package com.example.ai.service;

import com.example.ai.dto.response.ApiResponse;
import java.util.Map;

public interface AuthService {
    ApiResponse<Map<String, Object>> exchangeAuthCode(String code);
}
