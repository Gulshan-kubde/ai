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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

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
                document.setResumeUrl(null); // for future use (e.g. S3)
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
            byte[] resumeBytes = saved.getResumeData();
            byte[] photoBytes = saved.getPhotoData();
            byte[] docsBytes = saved.getSupportingDocsData();

            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            boas.write(saved.getResumeData());
            // ✅ Prepare Base64 safely (no prefix — clean for Blob display)
            return DocumentResponseDto.builder()
                    .docId(saved.getDocId())
                    .userId(userId)
                   // .resumeBase64(toBase64(saved.getResumeData()))
                    .resumeBtye(boas.toByteArray())
                    .photoBase64(toBase64(saved.getPhotoData()))
                    .supportingDocsBase64(toBase64(saved.getSupportingDocsData()))
                    .uploadedAt(saved.getUploadedAt())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Error processing uploaded files", e);
        }
    }

    /**
     * Converts byte[] to clean Base64 (no data: prefix).
     */
    private String toBase64(byte[] data) {
        if (data == null || data.length == 0)
            return null;
        return Base64.getEncoder().encodeToString(data);
    }


}
