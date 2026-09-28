package com.islamnizami.filvevaultdevlab.service;

import com.islamnizami.filvevaultdevlab.config.StorageProperties;
import com.islamnizami.filvevaultdevlab.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CleanupService {

    private static final Logger log =
            LoggerFactory.getLogger(CleanupService.class);

    private final Path rootLocation;
    private final FileMetadataRepository metadataRepository;

    @Autowired
    public CleanupService(
            StorageProperties properties,
            FileMetadataRepository metadataRepository) {

        this.rootLocation = Paths.get(properties.getLocation());
        this.metadataRepository = metadataRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOrphanedFiles() {

        log.info("Cleaning orphaned files");

        try (Stream<Path> paths = Files.walk(this.rootLocation, 1)) {

            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {

                        String filename =
                                path.getFileName().toString();

                        boolean existsInDb =
                                metadataRepository
                                        .existsByStoredFileName(filename);

                        if (!existsInDb) {
                            try {
                                Files.delete(path);

                                log.info(
                                        "Deleted orphaned file: {}",
                                        filename
                                );

                            } catch (IOException e) {
                                log.error(
                                        "Failed to delete file: {}",
                                        filename,
                                        e
                                );
                            }
                        }
                    });

        } catch (IOException e) {
            log.error(
                    "Failed to read storage location",
                    e
            );
        }
    }
}