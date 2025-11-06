package com.example.ai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Placeholder for future S3 or local URLs
    private String resumeUrl;
    private String photoUrl;
    private String supportingDocsUrl;

    // Store actual files as binary data
    @Lob
    @Column(name = "resume_data")
    private byte[] resumeData;

    @Lob
    @Column(name = "photo_data")
    private byte[] photoData;

    @Lob
    @Column(name = "supporting_docs_data")
    private byte[] supportingDocsData;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
