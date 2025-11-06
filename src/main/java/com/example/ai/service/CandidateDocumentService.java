package com.example.ai.service;

import com.example.ai.dto.response.DocumentResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CandidateDocumentService {
    DocumentResponseDto uploadOrUpdateDocument(Long userId,
                                               MultipartFile resume,
                                               MultipartFile photo,
                                               MultipartFile supportingDocs) throws IOException;
}
