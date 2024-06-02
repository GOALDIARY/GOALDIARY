package project.goal.domain;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import project.goal.web.dto.GoalDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-02T17:19:30+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class GoalMapperImpl implements GoalMapper {

    @Override
    public Goal goalDtoToGoal(GoalDto dto) {
        if ( dto == null ) {
            return null;
        }

        Goal.GoalBuilder goal = Goal.builder();

        goal.title( dto.getTitle() );
        goal.startDate( stringToLocalDateTime( dto.getStartDate() ) );
        goal.endDate( stringToLocalDateTime( dto.getEndDate() ) );
        goal.status( dto.getStatus() );
        goal.repEmotion( dto.getRepEmotion() );

        return goal.build();
    }

    @Override
    public void updateGoalFromDto(GoalDto dto, Goal goal) {
        if ( dto == null ) {
            return;
        }

        goal.setTitle( dto.getTitle() );
        goal.setStartDate( stringToLocalDateTime( dto.getStartDate() ) );
        goal.setEndDate( stringToLocalDateTime( dto.getEndDate() ) );
    }

    @Override
    public GoalDto goalToGoalDto(Goal goal) {
        if ( goal == null ) {
            return null;
        }

        GoalDto goalDto = new GoalDto();

        goalDto.setTitle( goal.getTitle() );
        goalDto.setStartDate( localDateTimeToString( goal.getStartDate() ) );
        goalDto.setEndDate( localDateTimeToString( goal.getEndDate() ) );
        goalDto.setStatus( goal.getStatus() );
        goalDto.setRepEmotion( goal.getRepEmotion() );

        return goalDto;
    }

    @Override
    public List<GoalDto> goalsToGoalDtos(List<Goal> goals) {
        if ( goals == null ) {
            return null;
        }

        List<GoalDto> list = new ArrayList<GoalDto>( goals.size() );
        for ( Goal goal : goals ) {
            list.add( goalToGoalDto( goal ) );
        }

        return list;
    }
}
