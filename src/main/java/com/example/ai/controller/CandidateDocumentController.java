package com.example.ai.controller;

import com.example.ai.dto.response.DocumentResponseDto;
import com.example.ai.service.CandidateDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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


    @GetMapping("/documents/{userId}/download-resume")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long userId) {


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("resume_" + userId + ".pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(documentService.downloadResume(userId));
    }

    @GetMapping("/documents/{userId}/resume-base64")
    public ResponseEntity<String> getResumeBase64(@PathVariable Long userId) {

        // Return plain text, not JSON
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(documentService.getResumeBase64(userId));
    }
}
