package com.islamnizami.filvevaultdevlab.repository;

import com.islamnizami.filvevaultdevlab.model.entity.FileDownloadAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDownloadAuditRepository extends JpaRepository<FileDownloadAudit,Long> {
}
