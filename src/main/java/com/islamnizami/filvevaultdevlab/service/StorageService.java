package com.islamnizami.filvevaultdevlab.service;


import ch.qos.logback.core.util.StringUtil;
import com.islamnizami.filvevaultdevlab.config.StorageProperties;
import com.islamnizami.filvevaultdevlab.exception.FileNotFoundException;
import com.islamnizami.filvevaultdevlab.exception.StorageException;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {


    private final Path rootLocation;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "png", "jpg", "jpeg", "docx", "txt");

    @Autowired
    public StorageService(StorageProperties properties) {
        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
    }

    @PostConstruct
    public void init() {
        try{
            Files.createDirectories(rootLocation);
        }catch (IOException e){
            throw new StorageException("Could not initilaize storage location.",e);
        }
    }

    public String store(MultipartFile file){
        try{
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file.");
            }
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getFileExtension(originalFilename);

            if(!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())){
                throw new StorageException("Unsupported file format. " + extension);
            }

            String storedFilename = UUID.randomUUID().toString() + "." + extension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }


            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return storedFilename;
        }catch (IOException e){
            throw new StorageException("Failed to store file. "+e.getMessage());
        }
    }

    public Resource loadAsResource(String storedFilename) {
        try {
            Path file = rootLocation.resolve(storedFilename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file: " + storedFilename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + storedFilename);
        }
    }

    public void delete(String storedFilename) {
        try {
            Path file = rootLocation.resolve(storedFilename).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Could not delete file: " + storedFilename, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}
