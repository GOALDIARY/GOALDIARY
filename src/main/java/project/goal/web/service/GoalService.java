package project.goal.web.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.emotion.Rep_emotions;
import project.goal.domain.GoalStatus;
import project.subGoal.web.repository.SubGoalRepository;
import project.user.domain.User;
import project.goal.web.dto.GoalDto;
import project.goal.domain.Goal;
import project.goal.domain.GoalMapper;
import project.goal.web.repository.GoalRepository;
import project.user.web.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserRepository userRepository;
    private final SubGoalRepository subGoalRepository;

    @Transactional
    public Goal save(GoalDto dto, String loginId){
        User user = userRepository.findByLoginId(loginId);
        Goal newGoal = Goal.createGoal(goalMapper.goalDtoToGoal(dto), user);
        return goalRepository.save(newGoal);
    }

    public Optional<Goal> findOne(Long id){
        return goalRepository.findById(id);
    }

    public Optional<List<Goal>> findCurrentGoals(String sort, String loginId){
        Long userId = userRepository.findByLoginId(loginId).getId();
        List<Goal> goalList = new ArrayList<>();
        if(sort.equals("startDate")){
            goalList=goalRepository.findAllByCurrentGoal(Sort.by(Sort.Direction.ASC, "startDate")
                    .and(Sort.by(Sort.Direction.ASC, "title")), userId);
        }else if(sort.equals("endDate")){
            goalList=goalRepository.findAllByCurrentGoal(Sort.by(Sort.Direction.ASC, "endDate")
                    .and(Sort.by(Sort.Direction.ASC, "title")), userId);
        }
        return Optional.of(goalList);
    }

    public Optional<List<Goal>> findPastGoals(String sort, String loginId){
        Long userId = userRepository.findByLoginId(loginId).getId();
        List<Goal> goalList = new ArrayList<>();
        if(sort.equals("startDate")){
            goalList=goalRepository.findAllByPastGoal(Sort.by(Sort.Direction.ASC, "startDate")
                    .and(Sort.by(Sort.Direction.ASC, "title")), userId);
        }else if(sort.equals("endDate")){
            goalList=goalRepository.findAllByPastGoal(Sort.by(Sort.Direction.ASC, "endDate")
                    .and(Sort.by(Sort.Direction.ASC, "title")), userId);
        }
        return Optional.of(goalList);
    }

    public Optional<List<Goal>> findPastGoalsByEmotion(String loginId, String emotion) {
        Long userId = userRepository.findByLoginId(loginId).getId();
        Rep_emotions repEmotion = Rep_emotions.valueOf(emotion.toUpperCase());
        Sort sort = Sort.by(Sort.Direction.ASC, "startDate").and(Sort.by(Sort.Direction.ASC, "title"));
        List<Goal> goalList = goalRepository.findPastGoalByEmotion(sort, userId, repEmotion);
        return Optional.of(goalList);
    }

    @Transactional
    public void update(Goal goal, GoalDto dto) {
        goalMapper.updateGoalFromDto(dto,goal);
    }

    @Transactional
    public void delete(Goal goal) {
        subGoalRepository.deleteByGoalId(goal.getId());
        goalRepository.delete(goal);
    }

    @Transactional
    public void completeGoal(Goal goal, String status, String emotion){
        GoalStatus goalStatus = GoalStatus.valueOf(status.toUpperCase());
        Rep_emotions repEmotion = Rep_emotions.valueOf(emotion.toUpperCase());
        goal.setStatus(goalStatus);
        goal.setRepEmotion(repEmotion);
    }



}
