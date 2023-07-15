package com.monie.xpress.auth_config.user.services;

import com.monie.xpress.auth_config.user.data.models.XpressToken;
import com.monie.xpress.auth_config.user.data.repositories.XpressTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class XpressTokenServiceImpl implements XpressTokenService {
    private final XpressTokenRepository xpressTokenRepository;

    @Override
    public void saveToken(XpressToken xpressToken) {
        xpressTokenRepository.save(xpressToken);
    }

    @Override
    public Optional<XpressToken> getValidTokenByAnyToken(String anyToken) {
        return xpressTokenRepository.findValidTokenByToken(anyToken);
    }

    @Override
    public void revokeToken(String accessToken) {
        getValidTokenByAnyToken(accessToken)
                .ifPresent(xpressToken -> {
                    xpressToken.setRevoked(true);
                    xpressTokenRepository.save(xpressToken);
                });
    }

    @Override
    public boolean isTokenValid(String anyToken) {
        return getValidTokenByAnyToken(anyToken)
                .map(xpressToken -> !xpressToken.isRevoked())
                .orElse(false);
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Africa/Lagos") //schedule to run every midnight
    private void deleteAllRevokedTokens() {
        final List<XpressToken> allRevokedTokens =
                xpressTokenRepository.findAllInvalidTokens();
        if (!allRevokedTokens.isEmpty()) {
            xpressTokenRepository.deleteAll(allRevokedTokens);
        }
    }

    @Scheduled(cron = "0 0 */6 * * *", zone = "Africa/Lagos")   //scheduled to run every 6 hours daily
    private void setTokenExpiration() {
        final List<XpressToken> notExpiredTokens =
                xpressTokenRepository.findAllTokenNotExpired();
        notExpiredTokens.stream()
                .filter(
                        token -> token.getCreatedAt()
                                .plusDays(7)
                                .isBefore(LocalDateTime.now())
                )
                .forEach(token -> token.setExpired(true));
        xpressTokenRepository.saveAll(notExpiredTokens);
    }
}
