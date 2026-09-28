package com.islamnizami.filvevaultdevlab.controller;


import com.islamnizami.filvevaultdevlab.model.dto.DownloadDTO;
import com.islamnizami.filvevaultdevlab.model.dto.FileResponseDTO;
import com.islamnizami.filvevaultdevlab.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestHeader("X-User-Id") String ownerId
    ){
        FileResponseDTO response = fileService.uploadFile(file, title, description, category, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<FileResponseDTO>> listMyFiles(
            @RequestHeader("X-User-Id") String ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<FileResponseDTO> files = fileService.getUserFiles(ownerId, PageRequest.of(page, size));
        return ResponseEntity.ok(files);
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String requesterId) {

        DownloadDTO downloadData = fileService.prepareDownload(id, requesterId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadData.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadData.getOriginalFilename() + "\"")
                .body(downloadData.getResource());
    }
}
