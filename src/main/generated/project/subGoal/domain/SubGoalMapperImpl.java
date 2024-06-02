package project.subGoal.domain;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import project.subGoal.web.dto.SubGoalDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-02T17:19:30+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class SubGoalMapperImpl implements SubGoalMapper {

    @Override
    public SubGoal subGoalDtoToSubGoal(SubGoalDto dto) {
        if ( dto == null ) {
            return null;
        }

        SubGoal.SubGoalBuilder subGoal = SubGoal.builder();

        subGoal.title( dto.getTitle() );
        subGoal.startDate( stringToLocalDateTime( dto.getStartDate() ) );
        subGoal.endDate( stringToLocalDateTime( dto.getEndDate() ) );
        subGoal.status( dto.getStatus() );
        subGoal.repEmotion( dto.getRepEmotion() );

        return subGoal.build();
    }

    @Override
    public void updateSubGoalFromDto(SubGoalDto dto, SubGoal subGoal) {
        if ( dto == null ) {
            return;
        }

        subGoal.setTitle( dto.getTitle() );
        subGoal.setStartDate( stringToLocalDateTime( dto.getStartDate() ) );
        subGoal.setEndDate( stringToLocalDateTime( dto.getEndDate() ) );
    }

    @Override
    public SubGoalDto subGoalToSubGoalDto(SubGoal subGoal) {
        if ( subGoal == null ) {
            return null;
        }

        SubGoalDto subGoalDto = new SubGoalDto();

        subGoalDto.setTitle( subGoal.getTitle() );
        subGoalDto.setStartDate( localDateTimeToString( subGoal.getStartDate() ) );
        subGoalDto.setEndDate( localDateTimeToString( subGoal.getEndDate() ) );
        subGoalDto.setStatus( subGoal.getStatus() );
        subGoalDto.setRepEmotion( subGoal.getRepEmotion() );

        return subGoalDto;
    }

    @Override
    public List<SubGoalDto> subGoalsToSubGoalDtos(List<SubGoal> subGoals) {
        if ( subGoals == null ) {
            return null;
        }

        List<SubGoalDto> list = new ArrayList<SubGoalDto>( subGoals.size() );
        for ( SubGoal subGoal : subGoals ) {
            list.add( subGoalToSubGoalDto( subGoal ) );
        }

        return list;
    }
}
