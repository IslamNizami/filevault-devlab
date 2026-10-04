package com.islamnizami.filvevaultdevlab.service;

import com.islamnizami.filvevaultdevlab.exception.FileNotFoundException;
import com.islamnizami.filvevaultdevlab.model.dto.DownloadDTO;
import com.islamnizami.filvevaultdevlab.model.dto.FileResponseDTO;
import com.islamnizami.filvevaultdevlab.model.entity.FileDownloadAudit;
import com.islamnizami.filvevaultdevlab.model.entity.FileMetadata;
import com.islamnizami.filvevaultdevlab.repository.FileDownloadAuditRepository;
import com.islamnizami.filvevaultdevlab.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final StorageService storageService;
    private final FileMetadataRepository metadataRepository;
    private final FileDownloadAuditRepository auditRepository;

    @Transactional
    public FileResponseDTO uploadFile(MultipartFile file, String title, String description, String category, String ownerId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream inputStream = file.getInputStream()) {
                byte[] buffer = new byte[8192]; // 8 KB-lıq kiçik buffer (paket)
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String checksum = hexString.toString();

            String storedFilename = storageService.store(file);

            FileMetadata metadata = new FileMetadata();
            metadata.setOriginalFileName(file.getOriginalFilename());
            metadata.setStoredFileName(storedFilename);
            metadata.setContentType(file.getContentType());
            metadata.setSizeBytes(file.getSize());
            metadata.setTitle(title);
            metadata.setDescription(description);
            metadata.setCategory(category);
            metadata.setOwnerId(ownerId);

            metadata.setChecksum(checksum);

            FileMetadata savedMetadata = metadataRepository.save(metadata);

            return mapToResponseDTO(savedMetadata);

        } catch (Exception e) {
            throw new RuntimeException("Error calculating checksum or saving file", e);
        }
    }

    public Page<FileResponseDTO> getUserFiles(String ownerId, Pageable pageable) {
        return metadataRepository.findAllByOwnerId(ownerId, pageable)
                .map(this::mapToResponseDTO);
    }

    public DownloadDTO prepareDownload(UUID fileId, String requesterId) {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File metadata not found in DB"));

        if (!metadata.getOwnerId().equals(requesterId)) {
            throw new SecurityException("You do not have permission to download this file.");
        }


        auditRepository.save(new FileDownloadAudit(fileId,requesterId));
        Resource resource = storageService.loadAsResource(metadata.getStoredFileName());
        return new DownloadDTO(resource, metadata.getOriginalFileName(), metadata.getContentType());
    }

    private FileResponseDTO mapToResponseDTO(FileMetadata m) {
        return new FileResponseDTO(m.getId(), m.getOriginalFileName(), m.getTitle(), m.getCategory(), m.getSizeBytes(), m.getUploadedAt());
    }
}
