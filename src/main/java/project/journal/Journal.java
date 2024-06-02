package project.journal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.emotion.Emotion;
import project.emotion.Rep_emotions;
import project.keyword.Keyword;
import project.subGoal.domain.SubGoal;
import project.user.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOURNAL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subGoals_id")
    @JsonIgnore
    private SubGoal subGoal;

    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL)
    private List<Keyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL)
    private List<Emotion> emotions = new ArrayList<>();

    private String content; //일지

    /**@Enumerated(EnumType.STRING)
    private Writing_modes writing_mode; //작성 모드 [letter, voice]**/
    private String writing_mode;

    @Enumerated(EnumType.STRING)
    private Rep_emotions rep_emotion; //대표 감정 [happy,sad,angry,gloomy]

    private LocalDateTime writeDate; //작성시간

    private void addKeyword(Keyword keyword, User user) {
        keywords.add(keyword);
        keyword.setJournal(this);
        keyword.setUser(user);
    }

    private void addEmotion(Emotion emotion) {
        emotions.add(emotion);
        emotion.setJournal(this);
    }

    public static Journal createJournal(User user, SubGoal subGoal, List<Keyword> keywords, List<Emotion> emotions, String content, String writingMode, Rep_emotions repEmotion) {
        Journal journal = new Journal();
        journal.setSubGoal(subGoal);
        journal.setContent(content);

        for (Keyword keyword : keywords) {
            journal.addKeyword(keyword, user);
        }

        for (Emotion emotion : emotions) {
            journal.addEmotion(emotion);
        }

        journal.setWriting_mode(writingMode);
        journal.setRep_emotion(repEmotion);
        journal.setWriteDate(LocalDateTime.now());

        return journal;
    }




}
