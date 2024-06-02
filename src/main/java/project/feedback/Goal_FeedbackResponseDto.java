package project.feedback;

import lombok.*;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal_FeedbackResponseDto {

    private String goal;
    private String goalStatus;
    private String goal_repEmotion;
    private List<String> subGoalList;
    private List<String> subGoalStatusList;

}