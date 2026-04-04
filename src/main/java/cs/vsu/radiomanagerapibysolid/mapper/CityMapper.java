package cs.vsu.radiomanagerapibysolid.mapper;

import cs.vsu.radiomanagerapibysolid.dto.CityDto;
import cs.vsu.radiomanagerapibysolid.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDto toDto(City city);

    City toEntity(CityDto cityDto);

    List<CityDto> toDtoList(List<City> cityList);

    List<City> toEntityList(List<CityDto> cityDtoList);

    void updateEntityFromDto(CityDto dto, @MappingTarget City entity);

}
