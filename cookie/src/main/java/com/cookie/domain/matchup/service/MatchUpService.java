package com.cookie.domain.matchup.service;

import com.cookie.domain.matchup.dto.response.MatchUpHistoryResponse;
import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import com.cookie.domain.matchup.repository.MatchUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchUpService {
    private final MatchUpRepository matchUpRepository;

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
}
