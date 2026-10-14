package cs.vsu.radiomanagerapibysolid.mapper;

import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public abstract UserDto toDto(User user);

    @Mapping(target = "password", qualifiedByName = "encodePassword")
    public abstract User toEntity(UserDto userDto);

    public abstract List<UserDto> toDtoList(List<User> userList);

    public abstract List<User> toEntityList(List<UserDto> userDtoList);

    @Mapping(target = "password", qualifiedByName = "encodePassword",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromDto(UserDto dto, @MappingTarget User entity);

    @Named("encodePassword")
    protected String encodePassword(String rawPassword) {
        return rawPassword == null ? null : passwordEncoder.encode(rawPassword);
    }
}
