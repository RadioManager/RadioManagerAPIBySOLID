package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.config.BaseProperties;
import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.mapper.AudioRecordingMapper;
import cs.vsu.radiomanagerapibysolid.model.AudioRecording;
import cs.vsu.radiomanagerapibysolid.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanagerapibysolid.repository.AudioRecordingRep;
import cs.vsu.radiomanagerapibysolid.service.inter.AudioRecordingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class AudioRecordingServiceImpl implements AudioRecordingService {

    private final BaseProperties baseProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioRecordingServiceImpl.class);

    private AudioRecordingRep audioRecordingRep;

    private AudioRecordingMapper mapper;

    @Override
    public List<AudioRecordingDto> getAllRecordings() {
        LOGGER.debug("Fetching all recordings");
        return mapper.toDtoList(audioRecordingRep.findAll());
    }

    @Override
    public AudioRecordingDto getRecordingById(Long id) {
        LOGGER.debug("Fetching recording {}", id);
        return audioRecordingRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public List<AudioRecordingDto> getRecordingByUserId(Long userId) {
        LOGGER.debug("Fetching recordings by user {}", userId);
        return mapper.toDtoList(audioRecordingRep.findByUserId(userId));
    }

    @Override
    public List<AudioRecordingDto> getRecordingByStatus(ApprovalStatus status) {
        LOGGER.debug("Fetching recordings by status {}", status);
        return mapper.toDtoList(audioRecordingRep.findByApprovalStatus(status));
    }

    @Override
    public List<AudioRecordingDto> getRecordingByStatusAndUserId(ApprovalStatus status, Long userId) {
        LOGGER.debug("Fetching recordings by status {} and user {}", status, userId);
        return mapper.toDtoList(audioRecordingRep.findByApprovalStatusAndUserId(status, userId));
    }

    @Override
    public AudioRecordingDto createRecording(AudioRecordingDto recordingDto) {
        LOGGER.debug("Creating recording {}", recordingDto);
        AudioRecording audioRecording = mapper.toEntity(recordingDto);
        audioRecording.setId(null);
        audioRecording = audioRecordingRep.save(audioRecording);
        LOGGER.debug("Recording created with id {}", audioRecording.getId());
        return mapper.toDto(audioRecording);
    }

    @Override
    public AudioRecordingDto updateRecording(AudioRecordingDto recordingDto) {
        LOGGER.debug("Updating recording {}", recordingDto);
        Optional<AudioRecording> optionalAudioRecording = audioRecordingRep.findById(recordingDto.getId());
        if (optionalAudioRecording.isPresent()) {
            AudioRecording audioRecording = optionalAudioRecording.get();
            mapper.updateEntityFromDto(recordingDto, audioRecording);
            LOGGER.debug("Recording updated with id {}", audioRecording.getId());
            return mapper.toDto(audioRecording);
        }
        LOGGER.warn("Recording with id for update {} not found", recordingDto.getId());
        return null;
    }

    @Override
    public AudioRecordingDto deleteRecording(Long id) {
        LOGGER.debug("Deleting recording {}", id);
        Optional<AudioRecording> optionalAudioRecording = audioRecordingRep.findById(id);
        if (optionalAudioRecording.isPresent()) {
            AudioRecording audioRecording = optionalAudioRecording.get();
            audioRecordingRep.delete(audioRecording);
            LOGGER.debug("Recording deleted with id {}", audioRecording.getId());
            return mapper.toDto(audioRecording);
        }
        LOGGER.warn("Recording with id for delete {} not found", id);
        return null;
    }

    @Override
    public AudioRecordingDto updateRecordingStatus(Long id, ApprovalStatus approvalStatus) {
        LOGGER.debug("Updating status of recording {}", id);
        Optional<AudioRecording> optionalAudioRecording = audioRecordingRep.findById(id);
        if (optionalAudioRecording.isPresent()) {
            AudioRecording audioRecording = optionalAudioRecording.get();
            audioRecording.setApprovalStatus(approvalStatus);
            AudioRecording updatedAudioRecording = audioRecordingRep.save(audioRecording);
            LOGGER.debug("Recording status updated with id {}", updatedAudioRecording.getId());
            return mapper.toDto(updatedAudioRecording);
        }
        LOGGER.warn("Recording status with id for update {} not found", id);
        return null;
    }

    @Override
    public Double getCostByDuration(Double duration) {

        return baseProperties.getFilePrice() * duration;
    }

}
