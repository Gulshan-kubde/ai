package com.example.ai.service.impl;

import com.example.ai.dto.response.DocumentResponseDto;
import com.example.ai.exception.InvalidFileFormatException;
import com.example.ai.exception.ResourceNotFoundException;
import com.example.ai.model.CandidateDocument;
import com.example.ai.model.User;
import com.example.ai.repository.CandidateDocumentRepository;
import com.example.ai.repository.UserRepository;
import com.example.ai.service.CandidateDocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static com.example.ai.util.Base64Utils.toBase64;

@Service
@RequiredArgsConstructor
public class CandidateDocumentServiceImpl implements CandidateDocumentService {

    private static final List<String> ALLOWED_RESUME_TYPES = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final CandidateDocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DocumentResponseDto uploadOrUpdateDocument(Long userId,
                                                      MultipartFile resume,
                                                      MultipartFile photo,
                                                      MultipartFile supportingDocs) {

        try {
            // ✅ Verify user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // ✅ Fetch or create candidate document record
            CandidateDocument document = documentRepository.findByUserId(userId)
                    .orElse(CandidateDocument.builder().user(user).build());

            // ✅ Validate and set resume
            if (resume != null && !resume.isEmpty()) {
                String contentType = resume.getContentType();
                if (!ALLOWED_RESUME_TYPES.contains(contentType)) {
                    throw new InvalidFileFormatException("Resume must be a PDF or Word document");
                }
                document.setResumeData(resume.getBytes());
                document.setResumeUrl(null);
                byte[] resumeBytes = resume.getBytes();


            }

            // ✅ Handle photo
            if (photo != null && !photo.isEmpty()) {
                document.setPhotoData(photo.getBytes());
                document.setPhotoUrl(null);
            }

            // ✅ Handle supporting docs
            if (supportingDocs != null && !supportingDocs.isEmpty()) {
                document.setSupportingDocsData(supportingDocs.getBytes());
                document.setSupportingDocsUrl(null);
            }


            // ✅ Save or update record
            CandidateDocument saved = documentRepository.save(document);

            DocumentResponseDto response =DocumentResponseDto.builder()
                    .docId(saved.getDocId())
                    .userId(userId)
                    .resumeBase64(toBase64(saved.getResumeData()))
                    //.resumeBtye(saved.getResumeData())
                    .photoBase64(toBase64(saved.getPhotoData()))
                    .supportingDocsBase64(toBase64(saved.getSupportingDocsData()))
                    .uploadedAt(saved.getUploadedAt())
                    .build();

            if (response.getResumeBase64() != null) {
                try {
                    byte[] decoded = Base64.getDecoder().decode(response.getResumeBase64());
                    System.out.println("Base64 validation: Successfully decoded " + decoded.length + " bytes");
                } catch (IllegalArgumentException e) {
                    System.err.println("ERROR: Generated Base64 is invalid! " + e.getMessage());
                    throw new RuntimeException("Failed to generate valid Base64 encoding");
                }
            }

            // ✅ Prepare Base64 safely (no prefix — clean for Blob display)
            return response;

        } catch (IOException e) {
            throw new RuntimeException("Error processing uploaded files", e);
        }
    }

    @Override
    public byte[] downloadResume(Long userId) {
        CandidateDocument document = documentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (document.getResumeData() == null) {
            throw new ResourceNotFoundException("No resume found for user");
        }
        return document.getResumeData();
    }

    @Override
    public String getResumeBase64(Long userId) {
        CandidateDocument document = documentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (document.getResumeData() == null) {
            throw new ResourceNotFoundException("No resume found for user");
        }

        String base64 = Base64.getEncoder().encodeToString(document.getResumeData());
        return base64;
    }



}
