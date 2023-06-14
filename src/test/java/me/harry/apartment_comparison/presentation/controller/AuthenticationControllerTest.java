package me.harry.apartment_comparison.presentation.controller;

import me.harry.apartment_comparison.application.dto.request.LoginServiceRequest;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.application.exception.LoginFailException;
import me.harry.apartment_comparison.application.service.LoginService;
import me.harry.apartment_comparison.application.service.LogoutService;
import me.harry.apartment_comparison.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @MockBean
    private LogoutService logoutService;

    @BeforeEach
    void setUp() {
        given(loginService.login(new LoginServiceRequest("test@example.com", "1234")))
                .willReturn(new LoginResponse("some-access-token", "some-refresh-token"));

        given(loginService.login(new LoginServiceRequest("xxx", "1234")))
                .willThrow(new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));

        given(loginService.login(new LoginServiceRequest("test@example.com", "xxx")))
                .willThrow(new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));
    }


    @DisplayName("올바른 이메일과 비밀번호로 로그인 요청시 201을 반환한다.")
    @Test
    void loginSuccess() throws Exception {
        // given
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

        verify(logoutService).logout(any(String.class));
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
        String expiredToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_USER, Instant.now().minus(5, ChronoUnit.MINUTES));

        // when then
        mockMvc.perform(delete("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + expiredToken)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
