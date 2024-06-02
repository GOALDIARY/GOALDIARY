package project.goal.domain;


import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import project.goal.web.dto.GoalDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    @Mapping(source = "title", target="title")
    @Mapping(source=" startDate", target= "startDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "stringToLocalDateTime")
    Goal goalDtoToGoal(GoalDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "title", target="title")
    @Mapping(source=" startDate", target= "startDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "stringToLocalDateTime")
    void updateGoalFromDto(GoalDto dto, @MappingTarget Goal goal);

    @Mapping(source = "title", target="title")
    @Mapping(source=" startDate", target= "startDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "status", target="status")
    @Mapping(source="repEmotion", target="repEmotion")
    GoalDto goalToGoalDto(Goal goal);

    List<GoalDto> goalsToGoalDtos(List<Goal> goals);


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
