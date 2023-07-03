package me.harry.baedal.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
class MenuControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("인터셉터 테스트")
    @Test
    void accessInterceptorSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/menus")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
