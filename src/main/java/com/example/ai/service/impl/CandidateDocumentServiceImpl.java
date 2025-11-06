package com.example.ai.service.impl;

import com.example.ai.dto.response.DocumentResponseDto;
import com.example.ai.exception.ResourceNotFoundException;
import com.example.ai.model.CandidateDocument;
import com.example.ai.model.User;
import com.example.ai.repository.CandidateDocumentRepository;
import com.example.ai.repository.UserRepository;
import com.example.ai.service.CandidateDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CandidateDocumentServiceImpl implements CandidateDocumentService {

    private final CandidateDocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Override
    public DocumentResponseDto uploadOrUpdateDocument(Long userId,
                                                      MultipartFile resume,
                                                      MultipartFile photo,
                                                      MultipartFile supportingDocs) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        CandidateDocument document = documentRepository.findByUserId(userId)
                .orElse(CandidateDocument.builder().user(user).build());

        if (resume != null && !resume.isEmpty()) document.setResumeData(resume.getBytes());
        if (photo != null && !photo.isEmpty()) document.setPhotoData(photo.getBytes());
        if (supportingDocs != null && !supportingDocs.isEmpty()) document.setSupportingDocsData(supportingDocs.getBytes());

        CandidateDocument saved = documentRepository.save(document);

        return DocumentResponseDto.builder()
                .docId(saved.getDocId())
                .userId(userId)
                .resumeUrl(saved.getResumeData() != null
                        ? "data:application/pdf;base64," + Base64.getEncoder().encodeToString(saved.getResumeData())
                        : null)
                .photoUrl(null)
                .supportingDocsUrl(null)
                .uploadedAt(saved.getUploadedAt())
                .build();
    }


}
