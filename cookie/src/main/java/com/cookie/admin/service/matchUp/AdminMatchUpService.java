package com.cookie.admin.service.matchUp;

import com.cookie.admin.dto.request.AdminMatchUpRequest;
import com.cookie.admin.dto.response.*;
import com.cookie.admin.exception.MatchUpBadRequestException;
import com.cookie.domain.matchup.entity.CharmPoint;
import com.cookie.domain.matchup.entity.EmotionPoint;
import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.MatchUpMovie;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import com.cookie.domain.matchup.repository.CharmPointRepository;
import com.cookie.domain.matchup.repository.EmotionPointRepository;
import com.cookie.domain.matchup.repository.MatchUpMovieRepository;
import com.cookie.domain.matchup.repository.MatchUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMatchUpService {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

    private final MatchUpMovieRepository matchUpMovieRepository;
    private final MatchUpRepository matchUpRepository;

    private final CharmPointRepository charmPointRepository;
    private final EmotionPointRepository emotionPointRepository;

    @Transactional(readOnly = true)
    public AdminMatchUpsResponse getMatchUps() {

        List<MatchUp> nowMatchUpDates = matchUpRepository.findByStatusWithMovies(MatchUpStatus.NOW);
        List<AdminMatchUpInfo> nowMatchUps = convertAdminMatchUpInfo(nowMatchUpDates);

        List<MatchUp> pendingMatchUpDates = matchUpRepository.findByStatusWithMovies(MatchUpStatus.PENDING);
        List<AdminMatchUpInfo> pendingMatchUps = convertAdminMatchUpInfo(pendingMatchUpDates);

        List<MatchUp> expireMatchUpDates = matchUpRepository.findByStatusWithMovies(MatchUpStatus.EXPIRATION);
        List<AdminMatchUpInfo> expireMatchUps = convertAdminMatchUpInfo(expireMatchUpDates);

        return AdminMatchUpsResponse.builder()
                .nowMatchUps(nowMatchUps)
                .pendingMatchUps(pendingMatchUps)
                .expireMatchUps(expireMatchUps)
                .build();
    }


    @Transactional
    public void createMatchUp(AdminMatchUpRequest request) {

        if (request.getMatchUpMovies().size() != 2) {
            throw new MatchUpBadRequestException("영화 2개를 입력해주세요.");
        }

        List<MatchUp> pendingMatchUpDates = matchUpRepository.findByStatusWithMovies(MatchUpStatus.PENDING);

        if (!pendingMatchUpDates.isEmpty()) {
            throw new MatchUpBadRequestException("매치 업 등록은 1개씩만 할 수 있습니다.");
        }

        List<MatchUpMovie> matchUpMovies = request.getMatchUpMovies().stream()
                .map(movie -> {
                    EmotionPoint emotionPoint = emotionPointRepository.save(createEmotionPoint());
                    CharmPoint charmPoint = charmPointRepository.save(createCharmPoint());

                            return MatchUpMovie.builder()
                                    .movieTitle(movie.getMovieTitle())
                                    .moviePoster(movie.getPoster())
                                    .voteCount(0L)
                                    .win(false)
                                    .emotionPoint(emotionPoint)
                                    .charmPoint(charmPoint)
                                    .build();
                })
                .toList();

        matchUpMovieRepository.saveAll(matchUpMovies);

        MatchUp matchUp = MatchUp.builder()
                .title(request.getMatchTitle())
                .type(request.getMatchUpType())
                .status(MatchUpStatus.PENDING)
                .movie1(matchUpMovies.get(0))
                .movie2(matchUpMovies.get(1))
                .startAt(request.getStartTime())
                .endAt(request.getEndTime())
                .chatroom(null) // 수정 필요
                .build();

        matchUpRepository.save(matchUp);
    }

    @Transactional(readOnly = true)
    public AdminMatchUpDetailResponse getMatchUpDetail(Long matchUpId) {

        MatchUp matchUp = matchUpRepository.findById(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매치 정보가 존재하지 않습니다."));

        List<MatchUpMovie> matchUpMovies = List.of(matchUp.getMovie1(), matchUp.getMovie2());

        List<MovieMatchInfoForUpdate> matchInfos = matchUpMovies.stream()
                .map(movie -> MovieMatchInfoForUpdate.builder()
                        .movieId(movie.getId())
                        .movieTitle(movie.getMovieTitle())
                        .poster(movie.getMoviePoster())
                        .voteCount(movie.getVoteCount())
                        .build())
                .toList();

        return AdminMatchUpDetailResponse.builder()
                .matchUpTitle(matchUp.getTitle())
                .movieMatchInfo(matchInfos)
                .matchUpType(matchUp.getType())
                .startTime(matchUp.getStartAt().format(dateTimeFormatter))
                .endTime(matchUp.getEndAt().format(dateTimeFormatter))
                .createdAt(matchUp.getCreatedAt().format(dateTimeFormatter))
                .matchUpStatus(matchUp.getStatus())
                .build();
    }

    @Transactional
    public void updateMatchUp(Long matchUpId, AdminMatchUpRequest update) {

        MatchUp matchUp = matchUpRepository.findById(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매치 정보가 존재하지 않습니다."));

        if (matchUp.getStatus() != MatchUpStatus.PENDING) {
            throw new MatchUpBadRequestException("PENDING 상태인 매치 업 콘텐츠만 수정할 수 있습니다.");
        }

        if (update.getMatchTitle() != null) {
            matchUp.updateTitle(update.getMatchTitle());
        }

        if (update.getMatchUpType() != null) {
            matchUp.updateType(update.getMatchUpType());
        }

        if (update.getStartTime() != null) {
            matchUp.updateStartAt(update.getStartTime());
        }

        if (update.getEndTime() != null) {
            matchUp.updateEndAt(update.getEndTime());
        }

        if (update.getMatchUpMovies() != null && update.getMatchUpMovies().size() == 2) {
            matchUp.getMovie1().updateMatchMovie(
                    update.getMatchUpMovies().get(0).getMovieTitle(),
                    update.getMatchUpMovies().get(0).getPoster());

            matchUp.getMovie2().updateMatchMovie(
                    update.getMatchUpMovies().get(1).getMovieTitle(),
                    update.getMatchUpMovies().get(1).getPoster());
        }

        matchUpRepository.save(matchUp);
    }

    @Transactional
    public void deleteMatchUp(Long matchUpId) {

       matchUpRepository.findById(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매치 정보가 존재하지 않습니다."));

        matchUpRepository.deleteById(matchUpId);
    }

    private List<AdminMatchUpInfo> convertAdminMatchUpInfo(List<MatchUp> matchUps) {
        return matchUps.stream()
                .map(match -> {
                    String winner;
                    if (match.getStatus() == MatchUpStatus.PENDING || match.getStatus() == MatchUpStatus.NOW) {
                        winner = "-";
                    } else if (match.getStatus() == MatchUpStatus.EXPIRATION) {
                        winner = match.getMovie1().isWin() ?
                                match.getMovie1().getMovieTitle() : match.getMovie2().getMovieTitle();
                    } else {
                        winner = "-";
                    }

                    return AdminMatchUpInfo.builder()
                            .matchId(match.getId())
                            .matchUpTitle(match.getTitle())
                            .startTime(match.getStartAt().format(dateTimeFormatter))
                            .endTime(match.getEndAt().format(dateTimeFormatter))
                            .winner(winner)
                            .movieMatchInfo(
                                    List.of(
                                            MovieMatchInfo.builder()
                                                    .poster(match.getMovie1().getMoviePoster())
                                                    .movieTitle(match.getMovie1().getMovieTitle())
                                                    .build(),
                                            MovieMatchInfo.builder()
                                                    .poster(match.getMovie2().getMoviePoster())
                                                    .movieTitle(match.getMovie2().getMovieTitle())
                                                    .build()
                                    )
                            )
                            .build();
                })
                .toList();
    }

    private CharmPoint createCharmPoint() {
        return CharmPoint.builder()
                .ost(0L)
                .directing(0L)
                .story(0L)
                .dialogue(0L)
                .visual(0L)
                .acting(0L)
                .specialEffects(0L)
                .build();
    }

    private EmotionPoint createEmotionPoint() {
        return EmotionPoint.builder()
                .touching(0L)
                .angry(0L)
                .joy(0L)
                .immersion(0L)
                .excited(0L)
                .empathy(0L)
                .tension(0L)
                .build();
    }
}
