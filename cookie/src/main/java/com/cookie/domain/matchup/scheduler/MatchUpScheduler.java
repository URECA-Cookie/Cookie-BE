package com.cookie.domain.matchup.scheduler;

import com.cookie.domain.matchup.service.MatchUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchUpScheduler {
    private final MatchUpService matchUpService;

    @Scheduled(cron = "0 0 0 ? * MON")
    public void activateMatchUp() {
        log.info("Activating match-up: Setting PENDING to NOW");
        matchUpService.updateMatchUpStatusToNow();
    }

    @Scheduled(cron = "0 0 0 ? * SUN")
    public void expireMatchUp() {
        log.info("Expiring match-up: Setting NOW to EXPIRATION and determining winners");
        matchUpService.updateMatchUpStatusToExpired();
    }
}
