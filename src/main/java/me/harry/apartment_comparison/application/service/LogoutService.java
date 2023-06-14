package me.harry.apartment_comparison.application.service;

import lombok.extern.slf4j.Slf4j;
import me.harry.apartment_comparison.application.dto.request.LogoutServiceRequest;
import me.harry.apartment_comparison.domain.model.BlackList;
import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.repository.BlackListRepository;
import me.harry.apartment_comparison.domain.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class LogoutService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public LogoutService(RefreshTokenRepository refreshTokenRepository, BlackListRepository blackListRepository, RedisTemplate<String, Object> redisTemplate,
                         @Value("${jwt.access-expired-time}") long accessTokenExpireTime, @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.blackListRepository = blackListRepository;
        this.redisTemplate = redisTemplate;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public void logout(LogoutServiceRequest dto) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByUserId(dto.userId()).get();
            // if redis connected
            if (redisTemplate.getRequiredConnectionFactory().getConnection().ping() != null) {
                blackListRepository.saveAll(
                        List.of(
                                new BlackList(dto.accessToken(), accessTokenExpireTime),
                                new BlackList(refreshToken.getRefreshToken(), refreshTokenExpireTime))
                );
            }
        } catch (IllegalStateException e) {
            log.error("Redis is not Ready, Please Check Redis Connection");
        } catch (NoSuchElementException e) {
            log.error("Not found refreshToken, userId: " + dto.userId());
        }

        refreshTokenRepository.deleteByUserId(dto.userId());
    }
}
