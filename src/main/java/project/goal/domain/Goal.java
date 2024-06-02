package project.goal.domain;

import jakarta.persistence.*;
import lombok.*;
import project.emotion.Emotion;
import project.emotion.Rep_emotions;
import project.user.domain.User;

import java.time.LocalDateTime;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="goal_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name="start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name="end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Rep_emotions repEmotion;

    // Rep_emotions 필드에 대한 setter 메서드
    public void setRepEmotion(Rep_emotions repEmotion) {
        this.repEmotion = repEmotion;
    }




    public static Goal createGoal(Goal dtoToGoal, User user){
        dtoToGoal.setStatus(GoalStatus.ACTIVATE);
        dtoToGoal.setUser(user);
        return dtoToGoal;
    }



}
