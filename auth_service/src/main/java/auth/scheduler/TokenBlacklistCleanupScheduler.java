package auth.scheduler;

import auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklistCleanupScheduler {

    private final TokenBlacklistService tokenBlacklistService;

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeExpired() {
        int deleted = tokenBlacklistService.purgeExpiredTokens();
        log.info("Очистка blacklist refresh-токенов: удалено {} истёкших записей", deleted);
    }
}
