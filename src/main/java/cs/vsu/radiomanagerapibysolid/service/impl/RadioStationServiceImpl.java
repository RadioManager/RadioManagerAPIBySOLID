package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.RadioStationDto;
import cs.vsu.radiomanagerapibysolid.mapper.RadioStationMapper;
import cs.vsu.radiomanagerapibysolid.model.RadioStation;
import cs.vsu.radiomanagerapibysolid.repository.RadioStationRep;
import cs.vsu.radiomanagerapibysolid.service.inter.RadioStationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RadioStationServiceImpl implements RadioStationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RadioStationServiceImpl.class);

    private final RadioStationRep radioStationRep;

    private final RadioStationMapper mapper;

    @Override
    public List<RadioStationDto> getAllRadioStation() {
        LOGGER.debug("Fetching all radio stations");
        return mapper.toDtoList(radioStationRep.findAll());
    }

    @Override
    public RadioStationDto getRadioStationById(Long id) {
        LOGGER.debug("Fetching radio station by id: {}", id);
        return radioStationRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public RadioStationDto getRadioStationByRepresentativeId(Long id) {
        LOGGER.debug("Fetching radio station by representative id:  {}", id);
        return  radioStationRep.findByRepresentativeId(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public RadioStationDto getRadioStationByName(String name) {
        LOGGER.debug("Fetching radio station by name: {}", name);
        return  radioStationRep.findByName(name)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public List<RadioStationDto> getRadioStationsByFrequency(BigDecimal frequency) {
        LOGGER.debug("Fetching radio stations by frequency: {}", frequency);
        return mapper.toDtoList(radioStationRep.findByFrequency(frequency));
    }

    @Override
    public List<RadioStationDto> getRadioStationsByCityId(Long cityId) {
        LOGGER.debug("Fetching radio stations by city id: {}", cityId);
        return mapper.toDtoList(radioStationRep.findByCityId(cityId));
    }

    @Override
    public RadioStationDto createRadioStation(RadioStationDto radioStationDto) {
        LOGGER.debug("Creating radio station: {}", radioStationDto);
        RadioStation newRadioStation = mapper.toEntity(radioStationDto);
        newRadioStation.setId(null);
        newRadioStation = radioStationRep.save(newRadioStation);
        LOGGER.debug("Radio station created: {}", newRadioStation);
        return mapper.toDto(newRadioStation);
    }

    @Override
    public RadioStationDto updateRadioStation(RadioStationDto radioStationDto) {
        LOGGER.debug("Updating radio station: {}", radioStationDto);
        Optional<RadioStation> optional = radioStationRep.findById(radioStationDto.getId());
        if (optional.isPresent()) {
            RadioStation radioStation = optional.get();
            mapper.updateEntityFromDto(radioStationDto, radioStation);
            RadioStation updatedRadioStation = radioStationRep.save(radioStation);
            LOGGER.debug("Radio station updated: {}", updatedRadioStation);
            return mapper.toDto(updatedRadioStation);
        }
        LOGGER.warn("Radio station with id {} not found", radioStationDto.getId());
        return null;
    }

    @Override
    public boolean deleteRadioStation(Long id) {
        LOGGER.debug("Deleting radio station: {}", id);
        Optional<RadioStation> optional = radioStationRep.findById(id);
        if (optional.isPresent()) {
            RadioStation radioStation = optional.get();
            radioStationRep.delete(radioStation);
            LOGGER.debug("Radio station deleted: {}", radioStation);
            return true;
        }
        LOGGER.warn("Radio station with id: {} not found", id);
        return false;
    }

}
