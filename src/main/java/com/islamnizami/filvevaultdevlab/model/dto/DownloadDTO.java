package com.islamnizami.filvevaultdevlab.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
public class DownloadDTO {

    private final Resource resource;
    private final String originalFilename;
    private final String contentType;
}