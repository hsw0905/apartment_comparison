package me.harry.baedal.presentation.controller;

import me.harry.baedal.BaedalApplication;
import me.harry.baedal.domain.model.UserRole;
import me.harry.baedal.infrastructure.config.RedisConfig;
import me.harry.baedal.infrastructure.config.WebSecurityConfig;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.presentation.security.AuthenticationService;
import me.harry.baedal.presentation.security.TokenGenerator;
import me.harry.baedal.presentation.security.TokenType;
import me.harry.baedal.presentation.security.TokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ContextConfiguration(classes = {
        BaedalApplication.class,
        WebSecurityConfig.class,
        RedisConfig.class
})
public abstract class ControllerTest {
    protected static final String USER_ID = "UserId";
    protected static final String ADMIN_ID = "AdminId";

    @SpyBean
    protected AuthenticationService authenticationService;

    @SpyBean
    protected TokenGenerator tokenGenerator;

    @SpyBean
    protected TokenVerifier tokenVerifier;

    @SpyBean
    protected RedisDao redisDao;

    protected String userAccessToken;
    protected String userRefreshToken;
    protected String adminAccessToken;

    @BeforeEach
    void setUpTokenAndUserDetailsForAuthentication() {
        userAccessToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_USER,
                TokenType.ACCESS, Instant.now().plus(10, ChronoUnit.MINUTES));
        adminAccessToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_ADMIN,
                TokenType.ACCESS, Instant.now().plus(10, ChronoUnit.MINUTES));

        userRefreshToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_USER,
                TokenType.REFRESH, Instant.now().plus(30, ChronoUnit.MINUTES));

    }
}
