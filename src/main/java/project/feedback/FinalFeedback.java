package project.feedback;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FinalFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 적절한 생성 전략 선택
    private Long id; // 기본 키 필드
    private String finalFeedback;

    public FinalFeedback() {
    }

    public FinalFeedback(String finalFeedback) {
        this.finalFeedback = finalFeedback;
    }

    public String getFinalFeedback() {
        return finalFeedback;
    }

    public void setFinalFeedback(String finalFeedback) {
        this.finalFeedback = finalFeedback;
    }


}
