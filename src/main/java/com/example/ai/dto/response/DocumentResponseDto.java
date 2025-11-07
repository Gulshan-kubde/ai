package com.example.ai.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDto {
    private Long docId;
    private Long userId;
    private String resumeUrl;
    private String photoUrl;
    private String supportingDocsUrl;

    private String resumeBase64;
    private byte[] resumeBtye;
    private String photoBase64;
    private String supportingDocsBase64;

    private LocalDateTime uploadedAt;
}
