package me.harry.apartment_comparison.presentation.controller;

import me.harry.apartment_comparison.application.dto.request.LoginServiceRequest;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.application.exception.LoginFailException;
import me.harry.apartment_comparison.application.service.LoginService;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() {
        given(loginService.login(new LoginServiceRequest("test@example.com", "1234")))
                .willReturn(new LoginResponse("some-access-token", "some-refresh-token"));

        given(loginService.login(new LoginServiceRequest("xxx", "1234")))
                .willThrow(new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));

        given(loginService.login(new LoginServiceRequest("test@example.com", "xxx")))
                .willThrow(new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));
    }

    @DisplayName("인증이 필요한 API에 토큰이 없다면 403을 반환한다.")
    @Test
    void accessForbiddenWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("유효하지 않은 토큰으로 접근하면 403을 반환한다.")
    @Test
    void accessForbiddenWithIncorrectToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth")
                        .header("Authorization", "Bearer incorrectToken")
                )
                .andExpect(status().isForbidden());
    }

    @DisplayName("유효한 토큰으로 접근하면 200을 반환한다.")
    @Test
    void accessSuccessWithCorrectToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth")
                        .header("Authorization", "Bearer " + userAccessToken)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("유효기간이 지난 토큰으로 접근하면 401을 반환한다.")
    @Test
    void accessDeniedWithExpiredToken() throws Exception {
        // given
        String expiredToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_USER, Instant.now().minus(5, ChronoUnit.MINUTES));

        // when then
        mockMvc.perform(get("/api/v1/auth")
                        .header("Authorization", "Bearer " + expiredToken)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

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
}
