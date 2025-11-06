package com.example.ai.controller;

import com.example.ai.dto.response.DocumentResponseDto;
import com.example.ai.service.CandidateDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class CandidateDocumentController {

    private final CandidateDocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponseDto> uploadDocuments(
            @RequestParam Long userId,
            @RequestParam(required = false) MultipartFile resume,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) MultipartFile supportingDocs) {

        DocumentResponseDto response = null;
        try {
            response = documentService.uploadOrUpdateDocument(userId, resume, photo, supportingDocs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(response);

    }
}
