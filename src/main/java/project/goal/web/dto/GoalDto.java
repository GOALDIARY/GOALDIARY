package project.goal.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.emotion.Rep_emotions;
import project.goal.domain.GoalStatus;


@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {

    @NotBlank
    private String title;
    @NotBlank
    private  String startDate;
    @NotBlank
    private String endDate;

    private GoalStatus status;

    private Rep_emotions repEmotion;
}
