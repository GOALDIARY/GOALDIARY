package project.goal.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.emotion.Emotion;
import project.goal.domain.GoalMapper;
import project.goal.domain.GoalStatus;
import project.goal.web.dto.GoalDto;
import project.goal.domain.Goal;
import project.goal.web.service.GoalService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@Valid @RequestBody GoalDto dto, @AuthenticationPrincipal UserDetails user){
        Goal savedGoal = goalService.save(dto,user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGoal.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalDto> getGoal(@PathVariable("id") Long id) {
        Optional<Goal> goal = goalService.findOne(id);
        return goal.map(goalMapper::goalToGoalDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/current")
    public ResponseEntity<List<GoalDto>> currentList(@RequestParam(value="sort", defaultValue ="startDate") String sort,
                                                  @AuthenticationPrincipal UserDetails user){
        List<Goal> goalList = goalService.findCurrentGoals(sort, user.getUsername())
                .orElse(Collections.emptyList());
        return ResponseEntity.ok(goalMapper.goalsToGoalDtos(goalList));
    }

    @GetMapping("/past")
    public ResponseEntity<List<GoalDto>> pastList(@RequestParam(value="sort", defaultValue ="startDate") String sort,
                                               @AuthenticationPrincipal UserDetails user){
        List<Goal> goalList = goalService.findPastGoals(sort, user.getUsername())
                .orElse(Collections.emptyList());
        return ResponseEntity.ok(goalMapper.goalsToGoalDtos(goalList));
    }

    @GetMapping("/by-emotion")
    public ResponseEntity<List<GoalDto>> pastListByEmotion(@RequestParam(value="type", defaultValue ="happy") String emotion,
                                                     @AuthenticationPrincipal UserDetails user){
        List<Goal> goalList = goalService.findPastGoalsByEmotion(user.getUsername(),emotion)
                .orElse(Collections.emptyList());
        return ResponseEntity.ok(goalMapper.goalsToGoalDtos(goalList));
    }


    @PutMapping("/{id}/edit")
    public ResponseEntity edit(@PathVariable("id") Long id, @Valid @RequestBody GoalDto dto){
        Optional<Goal> goal = goalService.findOne(id);
        if(!goal.isPresent()){
            return ResponseEntity.notFound().build();
        }
        goalService.update(goal.get(), dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<Goal> goal = goalService.findOne(id);
        if(!goal.isPresent()){
            return ResponseEntity.notFound().build();
        }
        goalService.delete(goal.get());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity complete(@PathVariable("id") Long id, @RequestParam("status") String status,
                                             @RequestParam("emotion") String emotion) {
        Optional<Goal> goal = goalService.findOne(id);
        if(!goal.isPresent()){
            return ResponseEntity.notFound().build();
        }
        goalService.completeGoal(goal.get(), status, emotion);
        return ResponseEntity.noContent().build();
    }
}

