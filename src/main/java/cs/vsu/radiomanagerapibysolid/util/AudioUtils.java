package cs.vsu.radiomanagerapibysolid.util;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AudioUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioUtils.class);

    public static boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        String extension = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf(".") + 1) : "";

        return contentType != null && contentType.equals("audio/mpeg") && extension.equalsIgnoreCase("mp3");
    }

//    public static double getMp3Duration(MultipartFile file) throws IOException {
//        if (!isAudioFile(file)) {
//            throw new IllegalArgumentException(
//                    "Invalid file. Expected audio/mpeg with extension .mp3"
//            );
//        }
//
//        Path tempFile = Files.createTempFile("upload-", ".mp3");
//        try {
//            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(tempFile.toFile());
//            @SuppressWarnings("unchecked")
//            Map<String, Object> props = baseFileFormat.properties();
//
//            Long microseconds = (Long) props.get("duration");
//            if (microseconds == null) {
//                throw new RuntimeException("Duration property not found in MP3SPI");
//            }
//
//            return Math.round(microseconds / 1_000_000.0);
//        } catch (UnsupportedAudioFileException e) {
//            LOGGER.error("The file is not supported by AudioSystem: {}", file.getOriginalFilename(), e);
//            throw new RuntimeException("Unsupported audio format", e);
//        } finally {
//            // Удаляем временный файл
//            try {
//                Files.deleteIfExists(tempFile);
//            } catch (IOException ex) {
//                LOGGER.warn("Failed to delete a temporary file: {}", tempFile, ex);
//            }
//        }
//    }

    public static double getMp3Duration(MultipartFile file) {
        if (!isAudioFile(file)) {
            throw new IllegalArgumentException(
                    "Invalid file. Expected audio/mpeg with extension .mp3"
            );
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            byte[] fileBytes = file.getBytes();
            Path tempFile = Files.createTempFile("upload-", ".mp3");

            try {
                Files.write(tempFile, fileBytes);

                MP3File mp3File = (MP3File) AudioFileIO.read(tempFile.toFile());

                return mp3File.getAudioHeader().getTrackLength();
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (IOException | CannotReadException | TagException |
                 ReadOnlyFileException | InvalidAudioFrameException e) {
            LOGGER.error("Failed to read MP3 file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to read MP3 duration", e);
        }
    }

}
