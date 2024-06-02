package project.emotion;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmotionRepository {

    private final EntityManager em;

    public Emotion save(Emotion emotion) {
        em.persist(emotion);
        return emotion;
    }


    public Emotion findOne(Long emotionId) {
        return em.find(Emotion.class, emotionId);
    }
}