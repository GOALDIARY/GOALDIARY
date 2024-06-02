package project.Final;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FinalFeedbackController {

    private String storedFinalFeedback;

    @PostMapping("/receive-finalFeedback")
    public ResponseEntity<String> receiveFinalFeedback(@RequestBody Map<String, String> data) {

        String feedback = (String) data.get("feedback");

        System.out.println("feedback: " + feedback);

        return ResponseEntity.ok("Final feedback received and stored: " + storedFinalFeedback);
    }


    @GetMapping("/get-finalFeedback")
    public ResponseEntity<String> getFinalFeedback() {

        return ResponseEntity.ok(storedFinalFeedback);
    }

}
