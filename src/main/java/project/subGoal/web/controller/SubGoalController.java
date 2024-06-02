package project.subGoal.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.subGoal.domain.SubGoal;
import project.subGoal.domain.SubGoalMapper;
import project.subGoal.web.dto.SubGoalDto;
import project.subGoal.web.service.SubGoalService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("goals/{goalId}/subGoals")
public class SubGoalController {

    private final SubGoalService subGoalService;
    private final SubGoalMapper subGoalMapper;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@PathVariable("goalId") Long goalId, @Valid @RequestBody SubGoalDto dto){
        SubGoal savedSubGoal = subGoalService.save(dto,goalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSubGoal.getId());
    }

    @GetMapping("/{subGoalId}")
    public ResponseEntity<SubGoalDto> getSubGoal(@PathVariable("subGoalId") Long subGoalId) {
        Optional<SubGoal> subGoal = subGoalService.findOne(subGoalId);
        return subGoal.map(subGoalMapper::subGoalToSubGoalDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SubGoalDto>> list(@PathVariable("goalId") Long goalId){
        List<SubGoal> subGoalList = subGoalService.findAll(goalId)
                .orElse(Collections.emptyList());
        return ResponseEntity.ok(subGoalMapper.subGoalsToSubGoalDtos(subGoalList));
    }

    @GetMapping("/by-emotion")
    public ResponseEntity<List<SubGoalDto>> listByEmotion(@PathVariable("goalId") Long goalId,
                                                       @RequestParam(value="type", defaultValue ="happy") String emotion){
        List<SubGoal> subGoalList = subGoalService.findAllByEmotion(goalId, emotion)
                .orElse(Collections.emptyList());
        return ResponseEntity.ok(subGoalMapper.subGoalsToSubGoalDtos(subGoalList));
    }


    @PutMapping("/{subGoalId}/edit")
    public ResponseEntity edit(@PathVariable("subGoalId") Long subGoalId, @Valid @RequestBody SubGoalDto dto){
        Optional<SubGoal> subGoal = subGoalService.findOne(subGoalId);
        if(!subGoal.isPresent()){
            return ResponseEntity.notFound().build();
        }
        subGoalService.update(subGoal.get(), dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{subGoalId}/delete")
    public ResponseEntity delete(@PathVariable("subGoalId") Long subGoalId){
        Optional<SubGoal> subGoal = subGoalService.findOne(subGoalId);
        if(!subGoal.isPresent()){
            return ResponseEntity.notFound().build();
        }
        subGoalService.delete(subGoal.get());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{subGoalId}/complete")
    public ResponseEntity complete(@PathVariable("subGoalId") Long subGoalId, @RequestParam("status") String status,
                                   @RequestParam("emotion") String emotion) {
        Optional<SubGoal> subGoal = subGoalService.findOne(subGoalId);
        if(!subGoal.isPresent()){
            return ResponseEntity.notFound().build();
        }
        subGoalService.completeGoal(subGoal.get(), status, emotion);
        return ResponseEntity.noContent().build();
    }
}
