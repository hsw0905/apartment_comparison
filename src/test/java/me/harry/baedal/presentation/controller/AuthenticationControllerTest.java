package me.harry.baedal.presentation.controller;

import me.harry.baedal.application.dto.request.LoginServiceRequest;
import me.harry.baedal.application.dto.request.LogoutServiceRequest;
import me.harry.baedal.application.dto.request.RefreshServiceRequest;
import me.harry.baedal.application.dto.response.LoginResponse;
import me.harry.baedal.application.dto.response.RefreshResponse;
import me.harry.baedal.application.exception.BadRequestException;
import me.harry.baedal.application.exception.LoginFailException;
import me.harry.baedal.application.service.LoginService;
import me.harry.baedal.application.service.LogoutService;
import me.harry.baedal.application.service.RefreshTokenService;
import me.harry.baedal.domain.model.UserRole;
import me.harry.baedal.presentation.security.TokenType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest extends ControllerTest {
    private static final String INVALIDATE_REFRESH_TOKEN_MESSAGE = "유효한 refreshToken이 아닙니다.";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoginService loginService;
    @MockBean
    private LogoutService logoutService;
    @MockBean
    private RefreshTokenService refreshTokenService;
    @SpyBean
    private RedisTemplate<String, Object> redisTemplate;

    @DisplayName("올바른 이메일과 비밀번호로 로그인 요청시 201을 반환한다.")
    @Test
    void loginSuccess() throws Exception {
        // given
        given(loginService.login(new LoginServiceRequest("test@example.com", "1234")))
                .willReturn(new LoginResponse("some-access-token", "some-refresh-token"));
        String json = """
                {
                    "email": "test@example.com",
                    "password": "1234"
                }
                """;

        // when then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("accessToken")))
                .andExpect(content().string(containsString("refreshToken")));
    }

    @DisplayName("로그인 요청시 이메일이 올바르지 않으면 400을 반환한다.")
    @Test
    void loginFailWithIncorrectEmail() throws Exception {
        // given
        given(loginService.login(new LoginServiceRequest("xxx", "1234")))
                .willThrow(new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));
        String json = """
                {
                    "email": "xxx",
                    "password": "1234"
                }
                """;

        // when then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("로그인 요청시 비밀번호가 올바르지 않으면 400을 반환한다.")
    @Test
    void loginFailWithIncorrectPassword() throws Exception {
        // given
        given(loginService.login(new LoginServiceRequest("test@example.com", "xxx")))
                .willThrow(new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));
        String json = """
                {
                    "email": "test@example.com",
                    "password": "xxx"
                }
                """;

        // when then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("로그인한 사용자는 로그아웃 할 수 있다.")
    @Test
    void logoutSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        verify(logoutService).logout(any(LogoutServiceRequest.class));
    }

    @DisplayName("토큰이 없으면 로그아웃 할 수 없으며 403을 반환한다.")
    @Test
    void logoutFailWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("올바르지 않은 토큰으로 로그아웃 할 수 없으며 403을 반환한다.")
    @Test
    void logoutFailWithIncorrectToken() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/logout")
                        .header("Authorization", "Bearer incorrectToken")
                )
                .andExpect(status().isForbidden());
    }

    @DisplayName("유효기간이 지난 토큰으로 로그아웃 할 수 없으며 401을 반환한다.")
    @Test
    void logoutFailWithExpiredToken() throws Exception {
        // given
        String expiredToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_USER,
                TokenType.ACCESS, Instant.now().minus(5, ChronoUnit.MINUTES));

        // when then
        mockMvc.perform(delete("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + expiredToken)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("블랙리스트 토큰으로 로그아웃 할 수 없으며, 401을 반환한다.")
    @Test
    void logoutFailWithBlackListToken() throws Exception {
        // given
        String blacklistToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_USER,
                TokenType.ACCESS, Instant.now().plus(5, ChronoUnit.MINUTES));
        redisTemplate.opsForValue().set(blacklistToken, USER_ID);

        // when then
        mockMvc.perform(delete("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + blacklistToken)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("올바른 refreshToken으로 갱신 요청할 경우 토큰을 갱신할 수 있다.")
    @Test
    void refreshTokenSuccess() throws Exception {
        // given
        given(refreshTokenService.refresh(new RefreshServiceRequest(USER_ID, UserRole.ROLE_USER.toString(),
                TokenType.REFRESH.toString(), userRefreshToken)))
                .willReturn(new RefreshResponse("new-access-token", "new-refresh-token"));

        // when then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer " + userRefreshToken)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("accessToken")))
                .andExpect(content().string(containsString("refreshToken")));
    }

    @DisplayName("accessToken으로 갱신 요청할 경우 토큰을 갱신할 수 없으며, 400을 반환한다.")
    @Test
    void refreshTokenFailWithAccessToken() throws Exception {
        // given
        given(refreshTokenService.refresh(new RefreshServiceRequest(USER_ID, UserRole.ROLE_USER.toString(),
                TokenType.ACCESS.toString(), userAccessToken))).willThrow(new BadRequestException(INVALIDATE_REFRESH_TOKEN_MESSAGE));

        // when then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer " + userAccessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
