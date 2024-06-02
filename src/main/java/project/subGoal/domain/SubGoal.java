package project.subGoal.domain;


import jakarta.persistence.*;
import lombok.*;
import project.emotion.Rep_emotions;
import project.goal.domain.Goal;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subGoals")
public class SubGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="subGoal_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name="start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name="end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private SubGoalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="goal_id")
    private Goal goal;

    @Enumerated(EnumType.STRING)
    private Rep_emotions repEmotion;

    public static SubGoal createSubGoal(SubGoal dtoToSubGoal, Goal goal){
        dtoToSubGoal.setStatus(SubGoalStatus.ACTIVATE);
        dtoToSubGoal.setGoal(goal);
        return dtoToSubGoal;
    }

}
