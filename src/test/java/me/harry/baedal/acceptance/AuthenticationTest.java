package me.harry.baedal.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import me.harry.baedal.application.dto.request.LoginServiceRequest;
import me.harry.baedal.application.dto.response.LoginResponse;
import me.harry.baedal.application.service.LoginService;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserRole;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.infrastructure.repository.RefreshTokenRepository;
import me.harry.baedal.infrastructure.repository.UserRepository;
import me.harry.baedal.presentation.dto.request.LoginRequest;
import me.harry.baedal.presentation.security.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class AuthenticationTest extends AcceptanceTest {
    private static String EMAIL = "test@example.com";
    private static String PASSWORD = "Abcd123!";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginService loginService;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        testUser = createUser("tester", EMAIL, PASSWORD, UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);

    }

    @DisplayName("사용자는 이메일과 비밀번호를 통해 로그인할 수 있다.")
    @Test
    void loginSuccess() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest(EMAIL, PASSWORD))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .log().all().statusCode(HttpStatus.CREATED.value());

    }

    @DisplayName("잘못된 이메일 형식일 경우 로그인에 실패하며, 400을 반환한다.")
    @Test
    void loginFailWithWrongEmail() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("xxx", PASSWORD))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .log().all().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("잘못된 비밀번호일 경우 로그인에 실패하며, 400을 반환한다.")
    @Test
    void loginFailWithWrongPassword() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest(EMAIL, "xxx"))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .log().all().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("로그인한 사용자는 로그아웃 할 수 있다.")
    @Test
    void logoutSuccess() {
        // given
        LoginResponse response = loginService.login(new LoginServiceRequest(EMAIL, PASSWORD));

        // when then
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + response.accessToken())
                .when()
                .delete("/api/v1/auth/logout")
                .then()
                .log().all().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("토큰이 없으면 로그아웃 할 수 없으며, 403을 반환한다.")
    @Test
    void logoutFailWithoutToken() {
        RestAssured.given().log().all()
                .header("Authorization", "")
                .when()
                .delete("/api/v1/auth/logout")
                .then()
                .log().all().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @DisplayName("토큰이 올바르지 않으면 로그아웃 할 수 없으며, 403을 반환한다.")
    @Test
    void logoutFailWithoutWrongToken() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer wrong-access-token")
                .when()
                .delete("/api/v1/auth/logout")
                .then()
                .log().all().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @DisplayName("만료된 토큰으로 로그아웃 할 수 없으며, 401을 반환한다.")
    @Test
    void logoutFailWithoutExpiredToken() {
        // given
        String expiredToken = tokenGenerator.generate(testUser.getId().toString(), UserRole.ROLE_USER,
                TokenType.ACCESS, Instant.now().minus(5, ChronoUnit.MINUTES));

        // when then
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + expiredToken)
                .when()
                .delete("/api/v1/auth/logout")
                .then()
                .log().all().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("블랙리스트 등록된 토큰으로 로그아웃 할 수 없으며, 401을 반환한다.")
    @Test
    void logoutFailWithoutBlacklistToken() {
        // given
        String blacklistToken = tokenGenerator.generate(testUser.getId().toString(), UserRole.ROLE_USER,
                TokenType.ACCESS, Instant.now().plus(5, ChronoUnit.MINUTES));
        redisDao.setValueWithExpireTime(blacklistToken, testUser.getId().toString(), 300, TimeUnit.SECONDS);
        // when then
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + blacklistToken)
                .when()
                .delete("/api/v1/auth/logout")
                .then()
                .log().all().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("사용자는 RefreshToken을 사용하여 토큰을 갱신할 수 있다.")
    @Test
    void refreshTokenSuccess() {
        // given
        LoginResponse response = loginService.login(new LoginServiceRequest(EMAIL, PASSWORD));

        // when then
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + response.refreshToken())
                .when()
                .post("/api/v1/auth/refresh")
                .then()
                .log().all().statusCode(HttpStatus.CREATED.value());
    }
}
