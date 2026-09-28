package com.islamnizami.filvevaultdevlab.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_metadata")
@Getter
@Setter
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false,unique = true)
    private String storedFileName;

    private String title;
    private String description;
    private String category;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false)
    private String ownerId; //authorized user id

    @Column(nullable = false,updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(length = 64)
    private String checksum;
}
