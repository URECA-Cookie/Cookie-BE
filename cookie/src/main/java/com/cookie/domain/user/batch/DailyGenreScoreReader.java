package com.cookie.domain.user.batch;

import com.cookie.domain.user.entity.DailyGenreScore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyGenreScoreReader implements ItemReader<DailyGenreScore> {

    @PersistenceContext
    private EntityManager entityManager;

    private List<DailyGenreScore> scores;
    private int currentIndex = 0;

    @Override
    public DailyGenreScore read() {
        // 처음 호출 시 데이터 조회
        if (scores == null) {
            scores = entityManager.createQuery(
                    "SELECT d FROM DailyGenreScore d", DailyGenreScore.class
            ).getResultList();
        }

        // 모든 데이터를 반환한 경우 null 반환
        if (currentIndex >= scores.size()) {
            return null;
        }

        // 리스트에서 하나씩 반환
        return scores.get(currentIndex++);
    }
}
