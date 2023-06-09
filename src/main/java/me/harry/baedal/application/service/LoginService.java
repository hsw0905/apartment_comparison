package me.harry.baedal.application.service;

import lombok.extern.slf4j.Slf4j;
import me.harry.baedal.application.dto.request.LoginServiceRequest;
import me.harry.baedal.application.dto.response.LoginResponse;
import me.harry.baedal.domain.exception.LoginFailException;
import me.harry.baedal.domain.model.user.RefreshToken;
import me.harry.baedal.domain.model.user.RefreshTokenId;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.infrastructure.repository.RefreshTokenRepository;
import me.harry.baedal.infrastructure.repository.UserRepository;
import me.harry.baedal.presentation.security.TokenGenerator;
import me.harry.baedal.presentation.security.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final RedisDao redisDao;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public LoginService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator,
                        RedisDao redisDao, @Value("${jwt.access-expired-time}") long accessTokenExpireTime, @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.redisDao = redisDao;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public LoginResponse login(LoginServiceRequest dto) {
        User user = getUser(dto);

        String accessToken = tokenGenerator.generate(user.getId().toString(), user.getRole(), TokenType.ACCESS, Instant.now().plusSeconds(accessTokenExpireTime));
        String refreshToken = tokenGenerator.generate(user.getId().toString(), user.getRole(), TokenType.REFRESH, Instant.now().plusSeconds(refreshTokenExpireTime));

        Optional<RefreshToken> entity = refreshTokenRepository.findByUser(user);

        if (entity.isPresent()) {
            RefreshToken oldRefreshToken = entity.get();

            addOldRefreshTokenToBlacklist(oldRefreshToken.getRefreshToken(), user.getId().toString());
            oldRefreshToken.changeRefreshToken(refreshToken);
        } else {
            refreshTokenRepository.save(new RefreshToken(RefreshTokenId.generate(), user, refreshToken));
        }

        return new LoginResponse(accessToken, refreshToken);
    }

    private User getUser(LoginServiceRequest dto) {
        return userRepository.findByEmail(dto.email())
                .filter(authUser -> passwordEncoder.matches(dto.password(), authUser.getEncodedPassword()))
                .orElseThrow(() -> new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));
    }

    private void addOldRefreshTokenToBlacklist(String oldRefreshToken, String userId) {
        try {
            redisDao.setValueWithExpireTime(oldRefreshToken, userId, refreshTokenExpireTime, TimeUnit.SECONDS);
        } catch (IllegalStateException e) {
            log.error("Redis is not Ready, Please Check Redis Connection - {}", e.getMessage(), e);
        }
    }
}
