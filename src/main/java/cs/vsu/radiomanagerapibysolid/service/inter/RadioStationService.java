package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.RadioStationDto;

import java.math.BigDecimal;
import java.util.List;

public interface RadioStationService {

    List<RadioStationDto> getAllRadioStation();

    RadioStationDto getRadioStationById(Long id);

    RadioStationDto getRadioStationByRepresentativeId(Long id);

    RadioStationDto getRadioStationByName(String name);

    List<RadioStationDto> getRadioStationsByFrequency(BigDecimal frequency);

    List<RadioStationDto> getRadioStationsByCityId(Long cityId);

    RadioStationDto createRadioStation(RadioStationDto radioStationDto);

    RadioStationDto updateRadioStation(RadioStationDto radioStationDto);

    boolean deleteRadioStation(Long id);

}
