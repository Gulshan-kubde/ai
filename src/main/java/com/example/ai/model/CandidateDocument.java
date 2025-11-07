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

    // Future fields for cloud storage URLs
    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "supporting_docs_url")
    private String supportingDocsUrl;

    // ✅ Use BYTEA explicitly for PostgreSQL instead of OID LOB
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "resume_data", columnDefinition = "BYTEA")
    private byte[] resumeData;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "photo_data", columnDefinition = "BYTEA")
    private byte[] photoData;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "supporting_docs_data", columnDefinition = "BYTEA")
    private byte[] supportingDocsData;

    @Column(name = "uploaded_at", nullable = true)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
