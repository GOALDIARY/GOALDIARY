package project.user.domain;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import project.user.web.dto.JoinDto;
import project.user.web.dto.UserDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-02T17:19:30+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User joinDtoToUser(JoinDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( dto.getName() );
        user.loginId( dto.getLoginId() );

        return user.build();
    }

    @Override
    public UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setName( user.getName() );
        userDto.setLoginId( user.getLoginId() );

        return userDto;
    }
}
