package com.cookie.domain.matchup.service;

import com.cookie.domain.matchup.dto.request.MatchUpVoteRequest;
import com.cookie.domain.matchup.dto.response.*;
import com.cookie.domain.matchup.dto.response.MainMatchUpsResponse.MainMatchUpResponse;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
        CharmPointResponse movie2CharmPoint = calculateCharmPointProportions(matchUp.getMovie2());

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

    @Transactional(readOnly = true)
    public MatchUpResponse getOnGoingMatchUp(Long matchUpId, Long userId) {
        MatchUp matchUp = matchUpRepository.findById(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("not found matchUpId: " + matchUpId));
        log.info("Retrieved matchUp: matchUpId = {}", matchUpId);

        CharmPointResponse movie1CharmPoint = calculateCharmPointProportions(matchUp.getMovie1());
        CharmPointResponse movie2CharmPoint = calculateCharmPointProportions(matchUp.getMovie2());

        EmotionPointResponse movie1EmotionPoint = calculateEmotionPointProportions(matchUp.getMovie1());
        EmotionPointResponse movie2EmotionPoint = calculateEmotionPointProportions(matchUp.getMovie2());

        MatchUpMovieResponse movie1 = MatchUpResponse.fromEntity(matchUp.getMovie1(), movie1CharmPoint, movie1EmotionPoint);
        MatchUpMovieResponse movie2 = MatchUpResponse.fromEntity(matchUp.getMovie2(), movie2CharmPoint, movie2EmotionPoint);

        boolean isVoted = hasUserVoted(userId, matchUp);

        return new MatchUpResponse(
                matchUp.getId(),
                matchUp.getTitle(),
                matchUp.getType(),
                matchUp.getStartAt(),
                matchUp.getEndAt(),
                movie1,
                movie2,
                isVoted
        );

    }


    private CharmPointResponse calculateCharmPointProportions(MatchUpMovie matchUpMovie) {
        long max = Arrays.stream(new long[]{
                matchUpMovie.getCharmPoint().getOst(),
                matchUpMovie.getCharmPoint().getDirecting(),
                matchUpMovie.getCharmPoint().getStory(),
                matchUpMovie.getCharmPoint().getDialogue(),
                matchUpMovie.getCharmPoint().getVisual(),
                matchUpMovie.getCharmPoint().getActing(),
                matchUpMovie.getCharmPoint().getSpecialEffects()
        }).max().orElse(0);

        if (max == 0) {
            return new CharmPointResponse(0, 0, 0, 0, 0, 0, 0);
        }

        return new CharmPointResponse(
                (int) ((matchUpMovie.getCharmPoint().getOst() * 100) / max),
                (int) ((matchUpMovie.getCharmPoint().getDirecting() * 100) / max),
                (int) ((matchUpMovie.getCharmPoint().getStory() * 100) / max),
                (int) ((matchUpMovie.getCharmPoint().getDialogue() * 100) / max),
                (int) ((matchUpMovie.getCharmPoint().getVisual() * 100) / max),
                (int) ((matchUpMovie.getCharmPoint().getActing() * 100) / max),
                (int) ((matchUpMovie.getCharmPoint().getSpecialEffects() * 100) / max)
        );
    }

    private EmotionPointResponse calculateEmotionPointProportions(MatchUpMovie matchUpMovie) {
        long max = Arrays.stream(new long[]{
                matchUpMovie.getEmotionPoint().getTouching(),
                matchUpMovie.getEmotionPoint().getAngry(),
                matchUpMovie.getEmotionPoint().getJoy(),
                matchUpMovie.getEmotionPoint().getImmersion(),
                matchUpMovie.getEmotionPoint().getExcited(),
                matchUpMovie.getEmotionPoint().getEmpathy(),
                matchUpMovie.getEmotionPoint().getTension()
        }).max().orElse(0);

        if (max == 0) {
            return new EmotionPointResponse(0, 0, 0, 0, 0, 0, 0);
        }

        return new EmotionPointResponse(
                (int) ((matchUpMovie.getEmotionPoint().getTouching() * 100) / max),
                (int) ((matchUpMovie.getEmotionPoint().getAngry() * 100) / max),
                (int) ((matchUpMovie.getEmotionPoint().getJoy() * 100) / max),
                (int) ((matchUpMovie.getEmotionPoint().getImmersion() * 100) / max),
                (int) ((matchUpMovie.getEmotionPoint().getExcited() * 100) / max),
                (int) ((matchUpMovie.getEmotionPoint().getEmpathy() * 100) / max),
                (int) ((matchUpMovie.getEmotionPoint().getTension() * 100) / max)
        );
    }

    private void checkIfUserAlreadyParticipated(Long userId, MatchUp matchUp) {
        boolean alreadyParticipated = matchUpParticipationRepository.existsByUserIdAndMatchUpMovie_Id(userId, matchUp.getMovie1().getId()) ||
                matchUpParticipationRepository.existsByUserIdAndMatchUpMovie_Id(userId, matchUp.getMovie2().getId());

        if (alreadyParticipated) {
            throw new IllegalArgumentException("이미 매치업 투표에 참여했습니다!");
        }
    }

    private boolean hasUserVoted(Long userId, MatchUp matchUp) {
        return matchUpParticipationRepository.existsByUserIdAndMatchUpMovie_Id(userId, matchUp.getMovie1().getId()) ||
                matchUpParticipationRepository.existsByUserIdAndMatchUpMovie_Id(userId, matchUp.getMovie2().getId());
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

    @Transactional
    public void updateMatchUpStatusToNow() {
        List<MatchUp> pendingMatchUps = matchUpRepository.findByStatus(MatchUpStatus.PENDING);
        for (MatchUp matchUp : pendingMatchUps) {
            matchUp.changeStatus(MatchUpStatus.NOW);
            matchUpRepository.save(matchUp);
        }
    }

    @Transactional
    public void updateMatchUpStatusToExpired() {
        List<MatchUp> nowMatchUps = matchUpRepository.findByStatus(MatchUpStatus.NOW);
        for (MatchUp matchUp : nowMatchUps) {
            matchUp.updateWinner(); // 우승자 갱신
            matchUp.changeStatus(MatchUpStatus.EXPIRATION);
            matchUpRepository.save(matchUp);
        }
    }

    @Cacheable(value = "mainMatchUpCache", cacheManager = "mainMatchUpCacheManager") // Caffeine Cache 적용
    public MainMatchUpsResponse getMainMatchUps() {
        List<MatchUp> nowMatchUps = matchUpRepository.findByStatus(MatchUpStatus.NOW);

        if (!nowMatchUps.isEmpty()) {
            List<MainMatchUpResponse> matchUps = nowMatchUps.stream()
                    .map(MainMatchUpResponse::fromEntity)
                    .toList();

            return new MainMatchUpsResponse(matchUps, true);
        }

        List<MatchUp> mostRecentExpiredMatchUps = matchUpRepository.findTop2ByStatusOrderByEndAtDesc(MatchUpStatus.EXPIRATION);

        if (!mostRecentExpiredMatchUps.isEmpty()) {
            List<MainMatchUpResponse> matchUps = mostRecentExpiredMatchUps.stream()
                    .map(MainMatchUpResponse::fromEntity)
                    .toList();

            return new MainMatchUpsResponse(matchUps, false);
        }

        return null;
    }

}

