package project.subGoal.web.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.emotion.Rep_emotions;
import project.goal.domain.Goal;
import project.goal.domain.GoalStatus;
import project.goal.web.repository.GoalRepository;
import project.subGoal.domain.SubGoal;
import project.subGoal.domain.SubGoalMapper;
import project.subGoal.domain.SubGoalStatus;
import project.subGoal.web.dto.SubGoalDto;
import project.subGoal.web.repository.SubGoalRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubGoalService {

    private final SubGoalRepository subGoalRepository;
    private final SubGoalMapper subGoalMapper;
    private final GoalRepository goalRepository;

    @Transactional
    public SubGoal save(SubGoalDto dto, Long goalId){
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new EntityNotFoundException("goal 반환 객체 없음"));
        SubGoal newSubGoal = SubGoal.createSubGoal(subGoalMapper.subGoalDtoToSubGoal(dto), goal);
        return subGoalRepository.save(newSubGoal);
    }

    public Optional<SubGoal> findOne(Long id){
        return subGoalRepository.findById(id);
    }

    public Optional<List<SubGoal>> findAll(Long goalId){
        List<SubGoal> subGoalList = subGoalRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate")
                                        .and(Sort.by(Sort.Direction.ASC, "title")), goalId);
        return Optional.of(subGoalList);
    }

    public Optional<List<SubGoal>> findAllByEmotion(Long goalId, String emotion) {
        Rep_emotions repEmotion = Rep_emotions.valueOf(emotion.toUpperCase());
        Sort sort = Sort.by(Sort.Direction.ASC, "startDate").and(Sort.by(Sort.Direction.ASC, "title"));
        List <SubGoal> subGoalList =  subGoalRepository.findAllByEmotion(sort, goalId, repEmotion);
        return Optional.of(subGoalList);
    }

    @Transactional
    public void update(SubGoal subGoal, SubGoalDto dto) {
        subGoalMapper.updateSubGoalFromDto(dto,subGoal);
    }

    @Transactional
    public void delete(SubGoal subGoal) {
        subGoalRepository.delete(subGoal);
    }

    @Transactional
    public void completeGoal(SubGoal subGoal, String status, String emotion) {
        SubGoalStatus subGoalStatus = SubGoalStatus.valueOf(status.toUpperCase());
        Rep_emotions repEmotion = Rep_emotions.valueOf(emotion.toUpperCase());
        subGoal.setStatus(subGoalStatus);
        subGoal.setRepEmotion(repEmotion);
    }
}