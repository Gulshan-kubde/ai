package com.example.ai.repository;

import com.example.ai.model.CandidateDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateDocumentRepository extends JpaRepository<CandidateDocument, Long> {

    Optional<CandidateDocument> findByUserId(Long userId);
}
