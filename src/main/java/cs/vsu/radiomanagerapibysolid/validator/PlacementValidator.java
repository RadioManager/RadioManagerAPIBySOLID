package cs.vsu.radiomanagerapibysolid.validator;

import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;
import cs.vsu.radiomanagerapibysolid.exception.ConflictException;
import cs.vsu.radiomanagerapibysolid.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Status;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PlacementValidator {

    public void validate(BroadcastSlotDto slot, AudioRecordingDto audio) {
        if (audio.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new ConflictException("Audio recording " + audio.getId() + " is not approved");
        }
        if (slot.getStatus() != Status.AVAILABLE) {
            throw new ConflictException("Broadcast slot " + slot.getId() + " is already occupied");
        }

        Duration audioDuration = Duration.ofSeconds(Math.round(audio.getDuration()));
        Duration slotDuration = Duration.between(slot.getStartTime(), slot.getEndTime());
        if (audioDuration.compareTo(slotDuration) >= 0) {
            throw new ConflictException("Audio recording " + audio.getId()
                    + " is longer than broadcast slot " + slot.getId());
        }
    }

}
