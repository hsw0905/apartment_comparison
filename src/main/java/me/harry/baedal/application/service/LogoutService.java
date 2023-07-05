package me.harry.baedal.application.service;

import lombok.extern.slf4j.Slf4j;
import me.harry.baedal.application.dto.request.LogoutServiceRequest;
import me.harry.baedal.application.exception.BadRequestException;
import me.harry.baedal.application.exception.NotFoundException;
import me.harry.baedal.domain.model.user.RefreshToken;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserId;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.infrastructure.repository.RefreshTokenRepository;
import me.harry.baedal.infrastructure.repository.UserRepository;
import me.harry.baedal.presentation.security.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LogoutService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RedisDao redisDao;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public LogoutService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
                         RedisDao redisDao, @Value("${jwt.access-expired-time}") long accessTokenExpireTime,
                         @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.redisDao = redisDao;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public void logout(LogoutServiceRequest dto) {
        validateAccessType(dto.tokenType());

        User user = getUser(dto.userId());
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUser(user);

        registerBlacklistToRedis(dto, refreshToken);
        refreshToken.ifPresent(entity -> refreshTokenRepository.deleteByUser(user));
    }

    private void registerBlacklistToRedis(LogoutServiceRequest dto, Optional<RefreshToken> refreshToken) {
        try {
            addOldAccessTokenToBlacklist(dto.accessToken(), dto.userId());
            refreshToken.ifPresent(entity -> addOldRefreshTokenToBlacklist(entity.getRefreshToken(), dto.userId()));
        } catch (IllegalStateException e) {
            log.error("Redis is not Ready, Please Check Redis Connection - {}", e.getMessage(), e);
        } catch (NoSuchElementException e) {
            log.error("Not found refreshToken, userId: " + dto.userId() + "- {}", e.getMessage(), e);
        }
    }

    private void addOldRefreshTokenToBlacklist(String oldRefreshToken, String userId) {
        redisDao.setValueWithExpireTime(oldRefreshToken, userId, refreshTokenExpireTime, TimeUnit.SECONDS);
    }

    private void addOldAccessTokenToBlacklist(String accessToken, String userId) {
        redisDao.setValueWithExpireTime(accessToken, userId, accessTokenExpireTime, TimeUnit.SECONDS);
    }

    private void validateAccessType(String tokenType) {
        if (!tokenType.equals(TokenType.ACCESS.toString())) {
            throw new BadRequestException("유효한 accessToken이 아닙니다.");
        }
    }

    private User getUser(String userId) {
        return userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    }
}
