package com.islamnizami.filvevaultdevlab.repository;

import com.islamnizami.filvevaultdevlab.model.entity.FileMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    Page<FileMetadata> findAllByOwnerId(String ownerId, Pageable pageable);
    boolean existsByStoredFileName(String storedFilename);

}
