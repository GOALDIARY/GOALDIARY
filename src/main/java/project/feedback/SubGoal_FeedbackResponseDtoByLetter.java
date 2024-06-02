package project.feedback;

import lombok.*;
import java.util.List;


@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubGoal_FeedbackResponseDtoByLetter {
    private String subGoal;
    private String journalContent;
    private List<String> positiveKeywordList;
}