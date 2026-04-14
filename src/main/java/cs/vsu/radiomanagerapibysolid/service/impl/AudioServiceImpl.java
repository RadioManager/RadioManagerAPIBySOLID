package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.service.inter.AudioService;
import cs.vsu.radiomanagerapibysolid.util.AudioUtils;
import cs.vsu.radiomanagerapibysolid.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class AudioServiceImpl implements AudioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioServiceImpl.class);

    private final String audioFilesDir;

    public AudioServiceImpl(@Value("${files.audio.directory}") String audioFilesDir) {
        this.audioFilesDir = audioFilesDir;
        createDirectory(Path.of(audioFilesDir));
    }

    private void createDirectory(Path directory) {
        FileUtils.createDirectory(directory);
    }

    public String saveFile(MultipartFile file, Long fileId) {
        return FileUtils.saveFile(file, Path.of(audioFilesDir), fileId);
    }

    public byte[] getFile(String filename) {
        return FileUtils.getFile(filename, Path.of(audioFilesDir));
    }

    public boolean deleteFile(String filename) {
        return FileUtils.deleteAudio(filename, Path.of(audioFilesDir));
    }

    public double getAudioDuration(MultipartFile file) {
        return AudioUtils.getMp3Duration(file);
    }
}
