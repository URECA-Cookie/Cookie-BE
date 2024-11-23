package com.cookie.domain.matchup.service;

import com.cookie.domain.matchup.dto.request.MatchUpVoteRequest;
import com.cookie.domain.matchup.dto.response.*;
import com.cookie.domain.matchup.entity.CharmPoint;
import com.cookie.domain.matchup.entity.EmotionPoint;
import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.MatchUpMovie;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import com.cookie.domain.matchup.repository.MatchUpMovieRepository;
import com.cookie.domain.matchup.repository.MatchUpRepository;
import com.cookie.domain.user.entity.MatchUpParticipation;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.MatchUpParticipationRepository;
import com.cookie.domain.user.repository.UserRepository;
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
    private final MatchUpMovieRepository matchUpMovieRepository;
    private final MatchUpParticipationRepository matchUpParticipationRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public void addMatchUpVote(Long userId, Long matchUpId, Long matchUpMovieId, MatchUpVoteRequest matchUpVoteRequest) {
        MatchUp matchUp = matchUpRepository.findById(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("not found matchUpId: " + matchUpId));
        log.info("Retrieved matchUp: matchUpId = {}", matchUpId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}", userId);

        MatchUpMovie selectedMovie = matchUpMovieRepository.findById(matchUpMovieId)
                .orElseThrow(() -> new IllegalArgumentException("not found matchUpMovieId: " + matchUpMovieId));
        log.info("Retrieved selected movie: matchUpMovieId = {}", matchUpMovieId);

        checkIfUserAlreadyParticipated(userId, matchUp);
        updatePoints(selectedMovie, matchUpVoteRequest);

        MatchUpParticipation matchUpParticipation = MatchUpParticipation.builder()
                .user(user)
                .matchUpMovie(selectedMovie)
                .build();

        matchUpParticipationRepository.save(matchUpParticipation);
        log.info("User added to matchUp participation: userId = {}, matchUpId = {}", userId, matchUpId);

        selectedMovie.incrementVoteCount();
        matchUpMovieRepository.save(selectedMovie);
        log.info("Incremented like count for movie: matchUpMovieId = {}, new vote count = {}", matchUpMovieId, selectedMovie.getVoteCount());

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

    private void checkIfUserAlreadyParticipated(Long userId, MatchUp matchUp) {
        boolean alreadyParticipated = matchUpParticipationRepository.existsByUserIdAndMatchUpMovie_Id(userId, matchUp.getMovie1().getId()) ||
                matchUpParticipationRepository.existsByUserIdAndMatchUpMovie_Id(userId, matchUp.getMovie2().getId());

        if (alreadyParticipated) {
            throw new IllegalArgumentException("이미 매치업 투표에 참여했습니다!");
        }
    }

    private void updatePoints(MatchUpMovie selectedMovie, MatchUpVoteRequest matchUpVoteRequest) {
        CharmPoint charmPoint = selectedMovie.getCharmPoint();
        EmotionPoint emotionPoint = selectedMovie.getEmotionPoint();

        if (charmPoint != null && matchUpVoteRequest.getCharmPoint() != null) {
            charmPoint.updatePoints(
                    matchUpVoteRequest.getCharmPoint().getOst(),
                    matchUpVoteRequest.getCharmPoint().getDirection(),
                    matchUpVoteRequest.getCharmPoint().getStory(),
                    matchUpVoteRequest.getCharmPoint().getDialogue(),
                    matchUpVoteRequest.getCharmPoint().getVisual(),
                    matchUpVoteRequest.getCharmPoint().getActing(),
                    matchUpVoteRequest.getCharmPoint().getSpecialEffect()
            );
        }

        if (emotionPoint != null && matchUpVoteRequest.getEmotionPoint() != null) {
            emotionPoint.updatePoints(
                    matchUpVoteRequest.getEmotionPoint().getTouching(),
                    matchUpVoteRequest.getEmotionPoint().getAngry(),
                    matchUpVoteRequest.getEmotionPoint().getJoy(),
                    matchUpVoteRequest.getEmotionPoint().getImmersion(),
                    matchUpVoteRequest.getEmotionPoint().getExcited(),
                    matchUpVoteRequest.getEmotionPoint().getEmpathy(),
                    matchUpVoteRequest.getEmotionPoint().getTension()
            );
        }
    }

}

