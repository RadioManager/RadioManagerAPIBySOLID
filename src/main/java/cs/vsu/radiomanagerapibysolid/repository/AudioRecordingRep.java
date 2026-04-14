package cs.vsu.radiomanagerapibysolid.repository;

import cs.vsu.radiomanagerapibysolid.model.AudioRecording;
import cs.vsu.radiomanagerapibysolid.model.enumerate.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AudioRecordingRep extends JpaRepository<AudioRecording, Integer> {

    Optional<AudioRecording> findById(Long id);
    List<AudioRecording> findByUserId(Long userId);
    List<AudioRecording> findByApprovalStatus(ApprovalStatus approvalStatus);
    List<AudioRecording> findByApprovalStatusAndUserId(ApprovalStatus approvalStatus, Long userId);

}
