package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.RadioStationDto;
import cs.vsu.radiomanagerapibysolid.model.auth.AuthModel;
import cs.vsu.radiomanagerapibysolid.service.inter.AccessService;
import cs.vsu.radiomanagerapibysolid.service.inter.AudioRecordingService;
import cs.vsu.radiomanagerapibysolid.service.inter.RadioStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("accessService")
@RequiredArgsConstructor
public class AccessServiceImpl implements AccessService {

    private final RadioStationService radioStationService;

    private final AudioRecordingService audioRecordingService;

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AuthModel authModel) {
            return authModel.getUserId();
        }
        return null;
    }

    @Override
    public boolean isOwnerOrAdmin(Long userId) {
        return isAdmin() || (userId != null && userId.equals(getCurrentUserId()));
    }

    @Override
    public boolean isStationOwnerOrAdmin(Long stationId) {
        if (isAdmin()) {
            return true;
        }
        RadioStationDto station = stationId != null ? radioStationService.getRadioStationById(stationId) : null;
        return station != null && isOwnerOrAdmin(station.getRepresentativeId());
    }

    @Override
    public boolean isRecordingOwnerOrAdmin(Long recordingId) {
        if (isAdmin()) {
            return true;
        }
        AudioRecordingDto recording = recordingId != null ? audioRecordingService.getRecordingById(recordingId) : null;
        return recording != null && isOwnerOrAdmin(recording.getUserId());
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

}
