package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.CityDto;

import java.util.List;

public interface CityService {

    List<CityDto> getAllCity();

    CityDto getCityById(Long id);

    CityDto getCityByNameAndRegion(String name, String region);

    List<CityDto> getCityByRegion(String region);

    List<CityDto> getCityByName(String name);

    CityDto createCity(CityDto cityDto);

    CityDto updateCity(CityDto cityDto);

    boolean deleteCity(Long id);

}
