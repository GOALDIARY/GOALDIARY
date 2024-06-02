package project.goal.web.repository;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.emotion.Rep_emotions;
import project.goal.domain.Goal;

import java.util.List;


@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {


    @Query("SELECT g FROM Goal g WHERE g.status = 'ACTIVATE' AND g.user.id = :userId")
    List<Goal> findAllByCurrentGoal(Sort sort, @Param("userId") Long userId);
    @Query("SELECT g FROM Goal g WHERE g.status IN ('SUCCESS', 'FAIL') AND g.user.id = :userId")
    List<Goal> findAllByPastGoal(Sort sort, @Param("userId") Long userId);

    @Query("SELECT g FROM Goal g WHERE g.status IN ('SUCCESS', 'FAIL') AND g.user.id = :userId AND g.repEmotion = :repEmotion")
    List<Goal> findPastGoalByEmotion(Sort sort, @Param("userId") Long userId, @Param("repEmotion") Rep_emotions repEmotion);

}
