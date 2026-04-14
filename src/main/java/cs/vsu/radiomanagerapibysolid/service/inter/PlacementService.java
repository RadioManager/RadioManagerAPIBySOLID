package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.PlacementDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PlacementService {

    List<PlacementDto> getAll();

    PlacementDto getById(Long id);

    List<PlacementDto> getByPlacementDate(LocalDateTime date);

    List<PlacementDto> getByAudioRecordingId(Long id);

    PlacementDto getByBroadcastSlotId(Long id);

    PlacementDto createPlacement(PlacementDto placementDto);

    PlacementDto updatePlacement(PlacementDto placementDto);

    boolean deletePlacement(Long id);

    Double getPlacementPrice(PlacementDto placementDto);

}
