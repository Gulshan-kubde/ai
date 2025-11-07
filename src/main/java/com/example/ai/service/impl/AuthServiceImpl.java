package com.example.ai.service.impl;

import com.example.ai.dto.response.ApiResponse;
import com.example.ai.dto.response.AuthData;
import com.example.ai.dto.response.UserResponseDto;
import com.example.ai.model.CandidateDocument;
import com.example.ai.model.User;
import com.example.ai.repository.CandidateDocumentRepository;
import com.example.ai.service.AuthService;
import com.example.ai.service.TempAuthCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Map;

import static com.example.ai.util.Base64Utils.toBase64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TempAuthCodeStore tempAuthCodeStore;
    private final CandidateDocumentRepository candidateDocumentRepository;

    @Override
    public ApiResponse<Map<String, Object>> exchangeAuthCode(String code) {
        AuthData authData = tempAuthCodeStore.get(code);

        if (authData == null) {
            return ApiResponse.error("","Invalid or expired auth code", 401, "/api/auth/exchange");
        }

        // Clean up once used
        tempAuthCodeStore.delete(code);

        User user = authData.getUser();
        CandidateDocument document = candidateDocumentRepository.findByUserId(user.getId()).orElse(null);

        UserResponseDto userDto = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .mobileNo(user.getMobileNo())
                .verified(user.isVerified())
                .resume(document != null ? toBase64(document.getResumeData()) : null)
                .photo(document != null ? toBase64(document.getPhotoData()) : null)
                .build();

        Map<String, Object> response = Map.of(
                "token", authData.getJwt(),
                "user", userDto
        );

        return ApiResponse.success(response, "Exchange successful", 200, "/api/auth/exchange");
    }

}
