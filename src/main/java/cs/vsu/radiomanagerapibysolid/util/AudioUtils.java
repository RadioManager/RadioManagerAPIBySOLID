package cs.vsu.radiomanagerapibysolid.util;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AudioUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioUtils.class);

    // TODO(ревью): новая константа — в Spring нет готового MediaType для mp3. Передаётся в
    //   FileUtils.getFileResponse(..., AudioUtils.AUDIO_MPEG) при отдаче файла записи.
    public static final MediaType AUDIO_MPEG = MediaType.valueOf("audio/mpeg");

    private AudioUtils(){
    }

    public static boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        String extension = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf(".") + 1) : "";

        return contentType != null && contentType.equals("audio/mpeg") && extension.equalsIgnoreCase("mp3");
    }

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
