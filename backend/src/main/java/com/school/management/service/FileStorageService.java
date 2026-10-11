package com.school.management.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "uploads/leave-attachments/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf", "image/jpeg", "image/png"
    );

    public FileStorageService() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Cannot store empty file.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed limit of 2 MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("Invalid file type. Only PDF, JPG, and PNG files under 2 MB are allowed.");
        }

        // Validate magic bytes
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int read = is.read(header);
            if (read < 4) {
                throw new RuntimeException("Invalid file header.");
            }
            if (!isPdf(header) && !isJpeg(header) && !isPng(header)) {
                throw new RuntimeException("File content does not match allowed format (PDF/JPG/PNG).");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to inspect file header: " + e.getMessage());
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String randomFilename = UUID.randomUUID().toString() + extension;
        Path targetLocation = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize().resolve(randomFilename);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return randomFilename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + randomFilename + ". Please try again!", e);
        }
    }

    public Path loadFileAsPath(String filename) {
        Path filePath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize().resolve(filename);
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found: " + filename);
        }
        return filePath;
    }

    private boolean isPdf(byte[] header) {
        return header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46; // %PDF
    }

    private boolean isJpeg(byte[] header) {
        return (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] header) {
        return (header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47;
    }
}
