package project.keyword;

import project.keyword.KeywordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public void saveKeyword(Keyword keyword) {
        keywordRepository.save(keyword);
    }

    public Set<Keyword> findAllByUserId(Long userId) {
        return keywordRepository.findAllByUserId(userId);
    }
}
