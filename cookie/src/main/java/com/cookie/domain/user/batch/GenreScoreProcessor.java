package com.cookie.domain.user.batch;

import com.cookie.domain.user.entity.DailyGenreScore;
import com.cookie.domain.user.entity.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenreScoreProcessor implements ItemProcessor<DailyGenreScore, GenreScoreUpdate> {

    @Override
    public GenreScoreUpdate process(DailyGenreScore dailyScore) {
        User user = dailyScore.getUser();
        String genre = dailyScore.getCategory();
        long score = dailyScore.getScore();

        // 단일 `GenreScoreUpdate` 반환
        return new GenreScoreUpdate(user, genre, score);
    }
}
