package com.islamnizami.filvevaultdevlab.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FileResponseDTO {
    private UUID id;
    private String originalFilename;
    private String title;
    private String category;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
}
