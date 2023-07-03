package me.harry.baedal.acceptance;

import io.restassured.RestAssured;
import me.harry.baedal.BaedalApplication;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserId;
import me.harry.baedal.domain.model.user.UserRole;
import me.harry.baedal.infrastructure.config.RedisConfig;
import me.harry.baedal.infrastructure.config.WebSecurityConfig;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.presentation.security.TokenGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {
        BaedalApplication.class,
        WebSecurityConfig.class,
        RedisConfig.class
})
public class AcceptanceTest {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected TokenGenerator tokenGenerator;

    @Autowired
    protected RedisDao redisDao;

    protected User testUser;

    protected String userAccessToken;

    protected String userRefreshToken;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected User createUser(String name, String email, String password, UserRole role, boolean isOut, boolean isAvailable) {
        return User.builder()
                .id(UserId.generate())
                .name(name)
                .email(email)
                .encodedPassword(passwordEncoder.encode(password))
                .role(role)
                .isOut(isOut)
                .isAvailable(isAvailable).build();
    }

    @AfterEach
    void tearDown() {
        redisDao.clear();
        databaseCleanup.execute();
    }
}
