package com.cookie.domain.user.batch;

import com.cookie.domain.user.entity.GenreScore;
import com.cookie.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GenreScoreWriter implements ItemWriter<GenreScoreUpdate> {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Map<String, String> CATEGORY_FIELD_MAP = Map.ofEntries(
            Map.entry("로맨스", "romance"),
            Map.entry("코미디", "comedy"),
            Map.entry("공포", "horror"),
            Map.entry("판타지", "fantasy"),
            Map.entry("액션", "action"),
            Map.entry("SF", "sf"),
            Map.entry("스릴러", "thriller"),
            Map.entry("음악", "music"),
            Map.entry("다큐멘터리", "documentary"),
            Map.entry("전쟁", "war"),
            Map.entry("애니메이션", "animation"),
            Map.entry("모험", "adventure"),
            Map.entry("드라마", "drama"),
            Map.entry("가족", "family"),
            Map.entry("미스터리", "mistery"),
            Map.entry("TV 영화", "tvMovie"),
            Map.entry("서부극", "western"),
            Map.entry("범죄", "crime"),
            Map.entry("역사", "history")

    );

    @Override
    public void write(Chunk<? extends GenreScoreUpdate> items) {
        Map<User, GenreScore> userGenreScores = new HashMap<>();

        // 1. 각 유저별 점수 합산
        for (GenreScoreUpdate update : items) {
            User user = update.getUser();
            String genre = update.getGenre();
            long score = update.getScore();

            // GenreScore 엔티티 가져오기 또는 새로 생성
            GenreScore genreScore = userGenreScores.computeIfAbsent(user, u -> {
                GenreScore newGenreScore = entityManager.createQuery(
                                "SELECT gs FROM GenreScore gs WHERE gs.user = :user", GenreScore.class)
                        .setParameter("user", user)
                        .getResultStream()
                        .findFirst()
                        .orElseGet(() -> GenreScore.builder().user(user).build());
                return newGenreScore;
            });

            // 점수 합산
            updateGenreScore(genreScore, genre, score);
        }

        // 2. 각 유저별로 총합 계산 및 백분율 반영
        for (Map.Entry<User, GenreScore> entry : userGenreScores.entrySet()) {
            User user = entry.getKey();
            GenreScore genreScore = entry.getValue();

            long totalScore = calculateTotalScore(genreScore);

            if (totalScore > 0) {
                updateGenreScorePercentages(genreScore, totalScore);
            }

            // 저장
            entityManager.merge(genreScore);
        }

        // 3. DailyGenreScore 데이터 삭제
        entityManager.createQuery(
                        "DELETE FROM DailyGenreScore d WHERE d.createdAt < :yesterday")
                .setParameter("yesterday", java.time.LocalDate.now().minusDays(1).atStartOfDay())
                .executeUpdate();
    }

    private void updateGenreScore(GenreScore genreScore, String genre, long score) {
        try {
            String fieldName = CATEGORY_FIELD_MAP.get(genre);
            if (fieldName == null) {
                throw new IllegalArgumentException("Unknown genre: " + genre);
            }

            Field field = GenreScore.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            long currentScore = (long) field.get(genreScore);
            field.set(genreScore, currentScore + score);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to update genre score for genre: " + genre, e);
        }
    }

    private long calculateTotalScore(GenreScore genreScore) {
        long totalScore = 0;
        for (String fieldName : CATEGORY_FIELD_MAP.values()) {
            try {
                Field field = GenreScore.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                totalScore += (long) field.get(genreScore);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to calculate total score", e);
            }
        }
        return totalScore;
    }

    private void updateGenreScorePercentages(GenreScore genreScore, long totalScore) {
        for (String fieldName : CATEGORY_FIELD_MAP.values()) {
            try {
                Field field = GenreScore.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                long currentScore = (long) field.get(genreScore);
                double percentage = (double) currentScore / totalScore * 100;

                // 필드 값을 백분율로 업데이트
                field.set(genreScore, Math.round(percentage));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to update percentages", e);
            }
        }
    }
}
