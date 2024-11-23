package com.cookie.domain.matchup.service;

import com.cookie.domain.matchup.dto.response.*;
import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.MatchUpMovie;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import com.cookie.domain.matchup.repository.MatchUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchUpService {
    private final MatchUpRepository matchUpRepository;

    @Transactional(readOnly = true)
    public List<MatchUpHistoryResponse> getMatchUpHistoryList() {
        List<MatchUp> expiredMatchUps = matchUpRepository.findByStatusWithMovies(MatchUpStatus.EXPIRATION);

        return expiredMatchUps.stream()
                .map(matchUp -> new MatchUpHistoryResponse(
                        matchUp.getId(),
                        matchUp.getTitle(),
                        matchUp.getStartAt(),
                        matchUp.getEndAt()
                ))
                .toList();

    }

    @Transactional
    public MatchUpHistoryDetailResponse getMatchUpHistoryDetail(Long matchUpId) {
        MatchUp matchUp = matchUpRepository.findMatchUpWithMoviesAndPoints(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("not found matchUpId: " + matchUpId));

        CharmPointResponse movie1CharmPoint = calculateCharmPointProportions(matchUp.getMovie1());
        CharmPointResponse movie2CharmPoint = calculateCharmPointProportions(matchUp.getMovie1());

        EmotionPointResponse movie1EmotionPoint = calculateEmotionPointProportions(matchUp.getMovie1());
        EmotionPointResponse movie2EmotionPoint = calculateEmotionPointProportions(matchUp.getMovie2());

        MatchUpMovieResponse movie1 = MatchUpHistoryDetailResponse.fromEntity(matchUp.getMovie1(), movie1CharmPoint, movie1EmotionPoint);
        MatchUpMovieResponse movie2 = MatchUpHistoryDetailResponse.fromEntity(matchUp.getMovie2(), movie2CharmPoint, movie2EmotionPoint);

        return new MatchUpHistoryDetailResponse(
                matchUp.getTitle(),
                matchUp.getType(),
                matchUp.getStartAt(),
                matchUp.getEndAt(),
                movie1,
                movie2
        );
    }

    private CharmPointResponse calculateCharmPointProportions(MatchUpMovie matchUpMovie) {
        long total = matchUpMovie.getCharmPoint().getOst() +
                matchUpMovie.getCharmPoint().getDirecting() +
                matchUpMovie.getCharmPoint().getStory() +
                matchUpMovie.getCharmPoint().getDialogue() +
                matchUpMovie.getCharmPoint().getVisual() +
                matchUpMovie.getCharmPoint().getActing() +
                matchUpMovie.getCharmPoint().getSpecialEffects();

        if (total == 0) {
            return new CharmPointResponse(0, 0, 0, 0, 0, 0, 0);
        }

        return new CharmPointResponse(
                (matchUpMovie.getCharmPoint().getOst() * 100) / total,
                (matchUpMovie.getCharmPoint().getDirecting() * 100) / total,
                (matchUpMovie.getCharmPoint().getStory() * 100) / total,
                (matchUpMovie.getCharmPoint().getDialogue() * 100) / total,
                (matchUpMovie.getCharmPoint().getVisual() * 100) / total,
                (matchUpMovie.getCharmPoint().getActing() * 100) / total,
                (matchUpMovie.getCharmPoint().getSpecialEffects() * 100) / total
        );
    }

    private EmotionPointResponse calculateEmotionPointProportions(MatchUpMovie matchUpMovie) {
        long total = matchUpMovie.getEmotionPoint().getTouching() +
                matchUpMovie.getEmotionPoint().getAngry() +
                matchUpMovie.getEmotionPoint().getJoy() +
                matchUpMovie.getEmotionPoint().getImmersion() +
                matchUpMovie.getEmotionPoint().getExcited() +
                matchUpMovie.getEmotionPoint().getEmpathy() +
                matchUpMovie.getEmotionPoint().getTension();

        if (total == 0) {
            return new EmotionPointResponse(0, 0, 0, 0, 0, 0, 0);
        }

        return new EmotionPointResponse(
                (matchUpMovie.getEmotionPoint().getTouching() * 100) / total,
                (matchUpMovie.getEmotionPoint().getAngry() * 100) / total,
                (matchUpMovie.getEmotionPoint().getJoy() * 100) / total,
                (matchUpMovie.getEmotionPoint().getImmersion() * 100) / total,
                (matchUpMovie.getEmotionPoint().getExcited() * 100) / total,
                (matchUpMovie.getEmotionPoint().getEmpathy() * 100) / total,
                (matchUpMovie.getEmotionPoint().getTension() * 100) / total
        );
    }
}

