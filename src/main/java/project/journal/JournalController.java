package project.journal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.user.domain.User;
import project.user.web.service.UserService;

@RestController
@RequestMapping("/subGoals/{subGoalId}")
public class JournalController {


 private final JournalService journalService;
 private final UserService userService;

 @Autowired
 public JournalController(JournalService journalService, UserService userService) {
  this.journalService = journalService;
  this.userService=userService;
 }

 // 일지를 저장하는 API
 @PostMapping("journal/save")
 public ResponseEntity<Journal> saveJournal(@RequestBody Journal journal, @PathVariable("subGoalId") Long subGoalId,  @AuthenticationPrincipal UserDetails user) {
  User user1 = userService.findByLoginId(user.getUsername());
  Journal savedJournal = journalService.saveJournal(journal, subGoalId, user1);
  return ResponseEntity.ok(savedJournal);
 }

 // ID로 일지를 가져오는 API
 @GetMapping("/get/{id}")
 public ResponseEntity<Journal> getJournalById(@PathVariable Long id) {
  Journal journal = journalService.getJournalById(id);
  if (journal != null) {
   return ResponseEntity.ok(journal);
  } else {
   return ResponseEntity.notFound().build();
  }
 }


}
