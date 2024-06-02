package project.user.web.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.exception.UserAlreadyExistsException;
import project.keyword.Keyword;
import project.keyword.KeywordService;
import project.user.domain.UserMapper;
import project.user.web.repository.UserRepository;
import project.user.web.dto.JoinDto;
import project.user.domain.User;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final KeywordService keywordService;

    @Transactional
    public User join(JoinDto dto){
        boolean isExist = userRepository.existsByLoginId(dto.getLoginId());
        if (isExist){
            throw new UserAlreadyExistsException("이미 존재하는 아이디입니다");
        }
        User newUser = userMapper.joinDtoToUser(dto);
        newUser.setPassword(encoder.encode(dto.getPassword()));
        newUser.setRole("ROLE_USER");
        User savedUser = userRepository.save(newUser);

        for (String keywordText : dto.getPositiveKeywordSet()) {
            Keyword keyword = new Keyword();
            keyword.setKeyword(keywordText);
            keyword.setUser(savedUser);
            keywordService.saveKeyword(keyword);
        }
        return newUser;
    }

    public User findByLoginId(String loginId){
        return userRepository.findByLoginId(loginId);
    }


}
