package project.user.domain;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.user.web.dto.JoinDto;
import project.user.web.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "name", target="name")
    @Mapping(source="loginId", target="loginId")
    User joinDtoToUser(JoinDto dto);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "loginId", target = "loginId")
    UserDto userToUserDto(User user);

}
