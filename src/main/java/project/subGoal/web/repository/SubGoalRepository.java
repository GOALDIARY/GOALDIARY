package project.subGoal.web.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.emotion.Rep_emotions;
import project.subGoal.domain.SubGoal;
import java.util.List;


@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long> {


    @Query("SELECT sg FROM SubGoal sg WHERE sg.goal.id = :goalId")
    List<SubGoal> findAll(Sort sort, @Param("goalId") Long goalId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SubGoal sg WHERE sg.goal.id = :goalId")
    void deleteByGoalId(@Param("goalId") Long goalId);

    @Query("SELECT sg FROM SubGoal sg WHERE sg.goal.id = :goalId AND sg.repEmotion = :repEmotion")
    List<SubGoal> findAllByEmotion(Sort sort, @Param("goalId") Long goalId, @Param("repEmotion") Rep_emotions repEmotion);
}
