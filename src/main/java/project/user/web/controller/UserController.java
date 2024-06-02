package project.user.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.user.web.dto.JoinDto;
import project.user.web.dto.UserDto;
import project.user.domain.User;
import project.user.domain.UserMapper;
import project.user.web.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/join")
    public ResponseEntity<?> create(@Valid @RequestBody JoinDto dto, BindingResult br){
        if (br.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        User user = userService.join(dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loginId = ((UserDetails) principal).getUsername();
        User user = userService.findByLoginId(loginId);
        UserDto userDto = userMapper.userToUserDto(user);
        return ResponseEntity.ok(userDto);
    }
}
