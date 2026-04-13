package cs.vsu.radiomanagerapibysolid.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Component

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static ResponseEntity<Resource> getFileResponse(byte[] fileData, String filename) {
        if (fileData != null) {
            ByteArrayResource resource = new ByteArrayResource(fileData);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    public static void createDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
            LOGGER.info("Created directory: {}", directory);
        } catch (IOException e) {
            LOGGER.error("Error creating directory: {}", directory, e);
            throw new RuntimeException("Error creating directory", e);
        }
    }

    public static String saveFile(MultipartFile file, Path directory, Long fileId) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            validateFilename(originalFilename);
            String filename = generateUniqueFilename(fileId, originalFilename);
            Path filepath = directory.resolve(Objects.requireNonNull(filename)).normalize();
            Files.write(filepath, file.getBytes());
            LOGGER.info("File saved: {}", filepath);
            return filename;
        } catch (IOException e) {
            LOGGER.error("Error saving file: {}", e.getMessage());
            throw new RuntimeException("Error saving file", e);
        }
    }

    public static String generateUniqueFilename(Long fileId, String originalFilename) {
        return fileId + "_" + originalFilename;
    }

    private static void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename is empty or null");
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\") || filename.contains("%00")) {
            throw new IllegalArgumentException("Invalid filename: contains forbidden characters");
        }
    }

    public static byte[] getFile(String filename, Path directory) {
        try {
            Path filepath = directory.resolve(filename);
            return Files.readAllBytes(filepath);
        } catch (IOException e) {
            LOGGER.error("Error reading file: {}", e.getMessage());
            throw new RuntimeException("Error reading file", e);
        }
    }

    public static boolean deleteAudio(String filename, Path directory) {
        try {
            Path filepath = directory.resolve(filename);
            Files.deleteIfExists(filepath);
            LOGGER.info("File deleted: {}", filepath);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error deleting file: {}", e.getMessage());
            throw new RuntimeException("Error deleting file", e);
        }
    }

}
