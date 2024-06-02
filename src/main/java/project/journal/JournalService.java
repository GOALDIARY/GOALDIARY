package project.journal;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subGoal.domain.SubGoal;
import project.subGoal.web.service.SubGoalService;
import project.user.domain.User;

@Service
@Transactional
public class JournalService {

    private final JournalRepository journalRepository;
    private final SubGoalService subGoalService;



    // 생성자 주입
    @Autowired
    public JournalService(JournalRepository journalRepository, SubGoalService subGoalService) {
        this.journalRepository = journalRepository;
        this.subGoalService=subGoalService;
    }


    public Journal saveJournal(Journal journal, Long subGoalId, User user) {
        SubGoal subGoal = subGoalService.findOne(subGoalId).orElseThrow(EntityNotFoundException::new);
        Journal newJournal = Journal.createJournal(user, subGoal, journal.getKeywords(), journal.getEmotions(), journal.getContent(), journal.getWriting_mode(), journal.getRep_emotion());
        return journalRepository.save(newJournal);
    }



    public Journal getJournalById(Long id) {
        return journalRepository.findById(id).orElse(null);
    }










}