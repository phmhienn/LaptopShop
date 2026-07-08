package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.BusinessException;
import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.service.FileStorageService;
import com.laptopstore.common.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${app.file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (!FileUtils.isValidImageFile(file)) {
            throw new BusinessException("Invalid file type or size exceeded limit.");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = FileUtils.generateFileName(originalFileName);

        try {
            if (fileName.contains("..")) {
                throw new BusinessException("Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(subDirectory);
            Files.createDirectories(targetLocation);
            
            Path targetFile = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new BusinessException("Could not store file " + fileName + ". Please try again!");
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName, String subDirectory) {
        try {
            Path filePath = this.fileStorageLocation.resolve(subDirectory).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + fileName);
        }
    }

    @Override
    public void deleteFile(String fileName, String subDirectory) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        
        try {
            Path filePath = this.fileStorageLocation.resolve(subDirectory).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new BusinessException("Could not delete file " + fileName);
        }
    }
}
