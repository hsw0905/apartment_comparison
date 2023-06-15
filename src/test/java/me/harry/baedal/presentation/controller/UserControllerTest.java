package me.harry.baedal.presentation.controller;

import me.harry.baedal.application.service.SignupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SignupService signupService;

    @DisplayName("올바른 입력값으로 회원 가입을 할 수 있다.")
    @Test
    void signupSuccess() throws Exception {
        // given
        String json = """
                    {
                        "email": "test@example.com",
                        "password": "Abcd123!",
                        "name": "tester"
                    }
                """;

        // when then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("이메일이 올바르지 않으면 회원가입 할 수 없다.")
    @Test
    void signupFailWithInvalidEmailFormat() throws Exception {
        // given
        String json = """
                    {
                        "email": "xxx",
                        "password": "Abcd123!",
                        "name": "tester"
                    }
                """;

        // when then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("이름이 빈칸이면 회원가입 할 수 없다.")
    @Test
    void signupFailWithInvalidNameFormat() throws Exception {
        // given
        String json = """
                    {
                        "email": "test@example.com",
                        "password": "Abcd123!",
                        "name": ""
                    }
                """;

        // when then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }
}
