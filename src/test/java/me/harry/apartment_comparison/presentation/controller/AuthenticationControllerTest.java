package me.harry.apartment_comparison.presentation.controller;

import me.harry.apartment_comparison.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
}
