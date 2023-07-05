package me.harry.baedal.application.service;

import lombok.extern.slf4j.Slf4j;
import me.harry.baedal.application.dto.request.RefreshServiceRequest;
import me.harry.baedal.application.dto.response.RefreshResponse;
import me.harry.baedal.application.exception.BadRequestException;
import me.harry.baedal.application.exception.NotFoundException;
import me.harry.baedal.domain.model.user.RefreshToken;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserId;
import me.harry.baedal.domain.model.user.UserRole;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.infrastructure.repository.RefreshTokenRepository;
import me.harry.baedal.infrastructure.repository.UserRepository;
import me.harry.baedal.presentation.security.TokenGenerator;
import me.harry.baedal.presentation.security.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RefreshTokenService {
    private static final String INVALIDATE_REFRESH_TOKEN_MESSAGE = "유효한 refreshToken이 아닙니다.";
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final RedisDao redisDao;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, TokenGenerator tokenGenerator, UserRepository userRepository,
                               RedisDao redisDao, @Value("${jwt.access-expired-time}") long accessTokenExpireTime,
                               @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
        this.redisDao = redisDao;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public RefreshResponse refresh(RefreshServiceRequest dto) {
        validateRefreshType(dto.tokenType());
        registerBlacklist(dto);

        String accessToken = tokenGenerator.generate(dto.userId(), UserRole.ROLE_USER, TokenType.ACCESS, Instant.now().plusSeconds(accessTokenExpireTime));
        String refreshToken = tokenGenerator.generate(dto.userId(), UserRole.ROLE_USER, TokenType.REFRESH, Instant.now().plusSeconds(refreshTokenExpireTime));

        updateRefreshToken(dto, refreshToken);

        return new RefreshResponse(accessToken, refreshToken);
    }

    private void updateRefreshToken(RefreshServiceRequest dto, String newRefreshToken) {
        User user = getUser(dto.userId());
        RefreshToken entity = getRefreshToken(user);

        entity.changeRefreshToken(newRefreshToken);
    }

    private void registerBlacklist(RefreshServiceRequest dto) {
        try {
            validateRefreshToken(dto.refreshToken());
            addOldRefreshTokenToBlacklist(dto);

        } catch (IllegalStateException e) {
            log.error("Redis is not Ready, Please Check Redis Connection -{} ", e.getMessage(), e);
            validateRefreshTokenInDatabase(dto);

        } catch (NoSuchElementException e) {
            log.error("Not found refreshToken, userId: " + dto.userId() + " -{}", e.getMessage(), e);
        }
    }

    private void addOldRefreshTokenToBlacklist(RefreshServiceRequest dto) {
        redisDao.setValueWithExpireTime(dto.refreshToken(), dto.userId(), refreshTokenExpireTime, TimeUnit.SECONDS);
    }

    private User getUser(String userId) {
        return userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    }

    private RefreshToken getRefreshToken(User user) {
        return refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Refresh Token을 찾을 수 없습니다. 재로그인이 필요합니다."));
    }

    private void validateRefreshTokenInDatabase(RefreshServiceRequest dto) {
        refreshTokenRepository.findByRefreshToken(dto.refreshToken())
                .orElseThrow(() -> new BadRequestException(INVALIDATE_REFRESH_TOKEN_MESSAGE));
    }

    private void validateRefreshType(String tokenType) {
        if (!tokenType.equals(TokenType.REFRESH.toString())) {
            log.error("Refresh Type이 아닙니다.");
            throw new BadRequestException(INVALIDATE_REFRESH_TOKEN_MESSAGE);
        }
    }

    private void validateRefreshToken(String refreshToken) {
        if (redisDao.findByKey(refreshToken).isPresent()) {
            log.error("[RefreshTokenService] Blacklist Token : " + refreshToken);
            throw new BadRequestException(INVALIDATE_REFRESH_TOKEN_MESSAGE);
        }
    }
}
