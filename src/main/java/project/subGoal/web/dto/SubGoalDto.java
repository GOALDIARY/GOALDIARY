package project.subGoal.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.emotion.Rep_emotions;
import project.subGoal.domain.SubGoalStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubGoalDto {

    @NotBlank
    private String title;
    @NotBlank
    private  String startDate;
    @NotBlank
    private String endDate;

    private SubGoalStatus status;

    private Rep_emotions repEmotion;
}
