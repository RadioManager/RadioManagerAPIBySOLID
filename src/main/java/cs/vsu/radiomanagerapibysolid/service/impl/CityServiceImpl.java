package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.CityDto;
import cs.vsu.radiomanagerapibysolid.mapper.CityMapper;
import cs.vsu.radiomanagerapibysolid.model.City;
import cs.vsu.radiomanagerapibysolid.repository.CityRep;
import cs.vsu.radiomanagerapibysolid.service.inter.CityService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CityServiceImpl implements CityService {

    private final Logger LOGGER = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityRep cityRep;

    private CityMapper mapper;

    @Override
    public List<CityDto> getAllCity() {
        LOGGER.debug("Fetching all cities");
        return mapper.toDtoList(cityRep.findAll());
    }

    @Override
    public CityDto getCityById(Long id) {
        LOGGER.debug("Fetching city by id: {}", id);
        return cityRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public CityDto getCityByNameAndRegion(String name, String region) {
        LOGGER.debug("Fetching city by name: {} and region: {}", name, region);
        return cityRep.findByNameAndRegion(name, region)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public List<CityDto> getCityByRegion(String region) {
        LOGGER.debug("Fetching city by region: {}", region);
        return mapper.toDtoList(cityRep.findByRegion(region));
    }

    @Override
    public List<CityDto> getCityByName(String name) {
        LOGGER.debug("Fetching city by name: {}", name);
        return mapper.toDtoList(cityRep.findByName(name));
    }

    @Override
    public CityDto createCity(CityDto cityDto) {
        LOGGER.debug("Creating city: {}", cityDto);
        City city = mapper.toEntity(cityDto);
        city.setId(null);
        city = cityRep.save(city);
        LOGGER.debug("City created: {}", city);
        return mapper.toDto(city);
    }

    @Override
    public CityDto updateCity(CityDto cityDto) {
        LOGGER.debug("Updating city: {}", cityDto);
        Optional<City> optional = cityRep.findById(cityDto.getId());
        if (optional.isPresent()) {
            City city = optional.get();
            mapper.updateEntityFromDto(cityDto, city);
            City updated = cityRep.save(city);
            LOGGER.debug("City updated: {}", updated);
            return mapper.toDto(updated);
        }
        LOGGER.warn("City with id {} not found", cityDto.getId());
        return null;
    }

    @Override
    public boolean deleteCity(Long id) {
        LOGGER.debug("Deleting city by id: {}", id);
        Optional<City> optional = cityRep.findById(id);
        if (optional.isPresent()) {
            City city = optional.get();
            cityRep.delete(city);
            LOGGER.debug("City deleted: {}", city);
            return true;
        }
        LOGGER.warn("City with id for delete {} not found", id);
        return false;
    }

}
