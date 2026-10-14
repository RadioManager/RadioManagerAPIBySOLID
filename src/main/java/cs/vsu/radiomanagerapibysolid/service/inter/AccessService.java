package cs.vsu.radiomanagerapibysolid.service.inter;

public interface AccessService {

    Long getCurrentUserId();

    boolean isOwnerOrAdmin(Long userId);

    boolean isStationOwnerOrAdmin(Long stationId);

    boolean isRecordingOwnerOrAdmin(Long recordingId);

}
