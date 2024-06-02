package project.feedback;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubGoal_FeedbackResponseDtoByVoice {
    private String subGoal;
    private MultipartFile journalContent;
    private List<String> positiveKeywordList;

}