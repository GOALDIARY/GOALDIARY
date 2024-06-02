package project.emotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.journal.Journal;


@Entity
@Getter@Setter
@Table(name="emotions")
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id")
    private Journal journal; //일지

    @Enumerated(EnumType.STRING)
    private Rep_emotions rep_emotion; //대표 감정 [HAPPY,ANGRY,SAD,NEUTRAL,DISGUST,HORROR]

    private int probability; //감정 확률

    private String emotion;

    public Emotion() {
    }

    public Emotion(String emotion) {
        this.emotion = emotion;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

}
