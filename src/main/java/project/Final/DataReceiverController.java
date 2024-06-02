package project.Final;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/receive_data")
public class DataReceiverController {

    private Map<String, Object> storedData = new HashMap<>();

    @PostMapping

    public String receiveData(@RequestBody Map<String, Object> data) {
        String emotion = (String) data.get("emotion");
        Map<String, Float> emotion_prob = (Map<String, Float>) data.get("emotion_prob");
        String feedback = (String) data.get("feedback");
        List<String> keywords = (List<String>) data.get("keywords");

        System.out.println("emotion: " + emotion);
        System.out.println("emotion_dict: " + emotion_prob);
        System.out.println("feedback: " + feedback);
        System.out.println("keywords: " + keywords);


        return "Data received successfully";
    }

    @GetMapping("/get_data")
    public ResponseEntity<Map<String, Object>> getData() {
        return ResponseEntity.ok(storedData);
    }


}
