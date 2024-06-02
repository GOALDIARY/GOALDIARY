package project.feedback;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import project.emotion.Rep_emotions;
import project.goal.domain.Goal;
import project.goal.web.service.GoalService;
import project.journal.JournalService;
import project.keyword.Keyword;
import project.keyword.KeywordService;
import project.subGoal.domain.SubGoal;
import project.subGoal.web.service.SubGoalService;
import project.user.web.service.UserService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    private final JournalService journalService;
    private final SubGoalService subGoalService;
    private final GoalService goalService;
    private final KeywordService keywordService;
    private final UserService userService;


    @PostMapping("/send/subGoals/{subGoalId}/journals/{journalId}/text")
    public ResponseEntity sendSubGoalTextFeedback(@PathVariable("subGoalId") Long subGoalId, @PathVariable("journalId") Long journalId
                                                    ,  @AuthenticationPrincipal UserDetails user) {
        Long userId = userService.findByLoginId(user.getUsername()).getId();
        Set<Keyword> keywordList = keywordService.findAllByUserId(userId);
        List<String> positiveKeywordList = new ArrayList<>();
        for (Keyword keyword : keywordList) {
            positiveKeywordList.add(keyword.getKeyword());
        }

        String subGoal = subGoalService.findOne(subGoalId).get().getTitle();
        String journalContent = journalService.getJournalById(journalId).getContent();


        SubGoal_FeedbackResponseDtoByLetter feedbackResponseDto = SubGoal_FeedbackResponseDtoByLetter.builder()
                .subGoal(subGoal)
                .journalContent(journalContent)
                .positiveKeywordList(positiveKeywordList).build();

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity("http://172.16.100.7:5000/feedback/0", feedbackResponseDto, Void.class);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("백->ai 전송 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/send/subGoals/{subGoalId}/journals/{journalId}/voice")
    public ResponseEntity sendSubGoalVoiceFeedback(@PathVariable("subGoalId") Long subGoalId,
                                                   @PathVariable("journalId") Long journalId, @AuthenticationPrincipal UserDetails user) throws IOException {
        String subGoal = subGoalService.findOne(subGoalId).get().getTitle();

        Long userId = userService.findByLoginId(user.getUsername()).getId();
        Set<Keyword> keywordList = keywordService.findAllByUserId(userId);
        List<String> positiveKeywordList = new ArrayList<>();
        for (Keyword keyword : keywordList) {
            positiveKeywordList.add(keyword.getKeyword());
        }

        try {
            String filePath = "C:\\Users\\shwng\\OneDrive\\바탕 화면\\캡스톤\\녹음.wav";
            File file = new File(filePath);

            if (!file.exists()) {
                throw new FileNotFoundException("파일 없음 " + filePath);
            }

            MultipartFile multipartFile = createMultipartFile(file);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("subGoal", subGoal);
            body.add("positiveKeywordList", String.join(",", positiveKeywordList));
            ByteArrayResource fileAsResource = new ByteArrayResource(multipartFile.getBytes()) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename();
                }
            };
            body.add("journalContent", fileAsResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            try {
                ResponseEntity<String> response = restTemplate.exchange("http://172.16.100.7:5000/feedback/1", HttpMethod.POST, requestEntity, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(response.getStatusCode()).build();
                }
            } catch (Exception e) {
                log.error("백->ai 전송 중 오류 발생", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch (IOException e) {
            log.error("파일 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private MultipartFile createMultipartFile(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return new CustomMultipartFile(file.getName(), fileContent, Files.probeContentType(file.toPath()));
    }

    @PostMapping("/send/goals/{goalId}")
    public ResponseEntity sendGoalFeedback(@PathVariable("goalId") Long goalId) {
        Goal goal = goalService.findOne(goalId).get();
        String goalTitle = goal.getTitle();
        String goalStatus = String.valueOf(goal.getStatus());
        String goalRepEmotion = String.valueOf(goal.getRepEmotion());
        List<SubGoal> subGoalList = subGoalService.findAll(goalId).orElse(null);

        goal.setRepEmotion(Rep_emotions.SAD);
        List<String> subGoalTitleList = new ArrayList<>();
        List<String> subGoalStatusList = new ArrayList<>();
        List<String> subGoalRepEmotionList = new ArrayList<>();
        for (SubGoal subGoal : subGoalList) {
            subGoalTitleList.add(subGoal.getTitle());
            subGoalRepEmotionList.add(String.valueOf(subGoal.getRepEmotion()));
            subGoalStatusList.add(String.valueOf(subGoal.getStatus()));
        }

        Goal_FeedbackResponseDto feedbackResponseDto = Goal_FeedbackResponseDto.builder()
                .goal(goalTitle)
                .goalStatus(goalStatus)
                .goal_repEmotion(goalRepEmotion)
                .subGoalList(subGoalTitleList)
                .subGoalStatusList(subGoalStatusList)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity("http://172.16.100.7:5000/final_feedback", feedbackResponseDto, Void.class);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("백->ai 전송 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}