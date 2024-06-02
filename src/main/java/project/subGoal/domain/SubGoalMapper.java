package project.subGoal.domain;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import project.subGoal.web.dto.SubGoalDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SubGoalMapper {

   SubGoalMapper INSTANCE = Mappers.getMapper(SubGoalMapper.class);

    @Mapping(source = "title", target="title")
    @Mapping(source=" startDate", target= "startDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "stringToLocalDateTime")
    SubGoal subGoalDtoToSubGoal(SubGoalDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "title", target="title")
    @Mapping(source=" startDate", target= "startDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "stringToLocalDateTime")
    void updateSubGoalFromDto(SubGoalDto dto, @MappingTarget SubGoal subGoal);

    @Mapping(source = "title", target="title")
    @Mapping(source=" startDate", target= "startDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "status", target="status")
    @Mapping(source="repEmotion", target="repEmotion")
    SubGoalDto subGoalToSubGoalDto(SubGoal subGoal);

    List<SubGoalDto> subGoalsToSubGoalDtos(List<SubGoal> subGoals);


    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String date) {
        return LocalDateTime.parse(date, FORMATTER);
    }
    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime date) {
        return FORMATTER.format(date);
    }

}
