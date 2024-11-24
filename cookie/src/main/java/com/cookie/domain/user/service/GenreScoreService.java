package com.cookie.domain.user.service;

import com.cookie.domain.user.entity.GenreScore;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.GenreScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreScoreService {

    private final GenreScoreRepository genreScoreRepository;

    @Transactional
    public void createAndSaveGenreScore(User user) {
        GenreScore genreScore = createGenreScore(user, user.getCategory().getId());
        genreScoreRepository.save(genreScore);
    }

    private GenreScore createGenreScore(User user, Long categoryId) {
        GenreScore.GenreScoreBuilder builder = GenreScore.builder()
                .user(user)
                .romance(0)
                .horror(0)
                .comedy(0)
                .action(0)
                .fantasy(0)
                .animation(0)
                .crime(0)
                .sf(0)
                .music(0)
                .thriller(0)
                .war(0)
                .documentary(0)
                .drama(0)
                .family(0)
                .history(0)
                .mistery(0)
                .tv_movie(0)
                .western(0)
                .adventure(0);

        switch (categoryId.intValue()) {
            case 1 -> builder.romance(5);
            case 2 -> builder.horror(5);
            case 3 -> builder.comedy(5);
            case 4 -> builder.action(5);
            case 5 -> builder.fantasy(5);
            case 6 -> builder.animation(5);
            case 7 -> builder.crime(5);
            case 8 -> builder.sf(5);
            case 9 -> builder.music(5);
            case 10 -> builder.thriller(5);
            case 11 -> builder.war(5);
            case 12 -> builder.documentary(5);
            case 13 -> builder.drama(5);
            case 14 -> builder.family(5);
            case 15 -> builder.history(5);
            case 16 -> builder.mistery(5);
            case 17 -> builder.tv_movie(5);
            case 18 -> builder.western(5);
            case 19 -> builder.adventure(5);
            default -> throw new IllegalArgumentException("Invalid category ID: " + categoryId);
        }

        return builder.build();
    }
}
