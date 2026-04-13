package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.model.enumerate.ApprovalStatus;

import java.util.List;

public interface AudioRecordingService {

    List<AudioRecordingDto> getAllRecordings();

    AudioRecordingDto getRecordingById(Long id);

    List<AudioRecordingDto> getRecordingByUserId(Long userId);

    List<AudioRecordingDto> getRecordingByStatus(ApprovalStatus status);

    List<AudioRecordingDto> getRecordingByStatusAndUserId(ApprovalStatus status, Long userId);

    AudioRecordingDto createRecording(AudioRecordingDto recordingDto);

    AudioRecordingDto updateRecording(AudioRecordingDto recordingDto);

    AudioRecordingDto deleteRecording(Long id);

    AudioRecordingDto updateRecordingStatus(Long id, ApprovalStatus approvalStatus);

    Double getCostByDuration(Double duration);

}
