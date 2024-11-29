package com.cookie.domain.user.service;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.user.entity.DailyGenreScore;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.ActionType;
import com.cookie.domain.user.repository.DailyGenreScoreRepository;
import com.cookie.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DailyGenreScoreService {

    private final DailyGenreScoreRepository dailyGenreScoreRepository;
    private final UserRepository userRepository;

    public DailyGenreScoreService(DailyGenreScoreRepository dailyGenreScoreRepository, UserRepository userRepository) {
        this.dailyGenreScoreRepository = dailyGenreScoreRepository;
        this.userRepository = userRepository;
    }

    public void saveScore(User user, String genre, long score, ActionType actionType) {
        DailyGenreScore dailyScore = DailyGenreScore.builder()
                .user(user)
                .category(genre)
                .score(score)
                .actionType(actionType)
                .build();
        dailyGenreScoreRepository.save(dailyScore);
    }

}

