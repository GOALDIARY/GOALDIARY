package project.keyword;

import project.keyword.Keyword;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor

public class KeywordRepository {

    private final EntityManager em;

    public void save(Keyword keyword) {
        em.persist(keyword);
    }

    public Keyword findOne(Long keywordId) {
        return em.find(Keyword.class, keywordId);
    }

    public Set<Keyword> findAllByUserId(Long userId) {
        return (Set<Keyword>) em.createQuery("SELECT k FROM Keyword k WHERE k.user.id = :userId", Keyword.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
