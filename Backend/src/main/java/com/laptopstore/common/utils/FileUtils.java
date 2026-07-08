package com.laptopstore.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

public final class FileUtils {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private FileUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static String generateFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + "." + extension;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public static boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType);
    }

    public static boolean isAllowedImageType(String contentType) {
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType);
    }
}
