package com.islamnizami.filvevaultdevlab.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_download_audit")
@Getter
@Setter
@NoArgsConstructor
public class FileDownloadAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID fileId;
    private String downloaderId;
    private LocalDateTime downloadedAt;

    public FileDownloadAudit(UUID fileId, String downloaderId) {
        this.fileId = fileId;
        this.downloaderId = downloaderId;
        this.downloadedAt = LocalDateTime.now();
    }
}
