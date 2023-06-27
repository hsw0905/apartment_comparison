package me.harry.baedal.presentation.controller;

import me.harry.baedal.application.dto.response.CategoryResponse;
import me.harry.baedal.application.service.GetCategoryListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GetCategoryListService getCategoryListService;

    @DisplayName("카테고리 리스트를 가져온다.")
    @Test
    void getCategoriesSuccess() throws Exception {
        given(getCategoryListService.getCategories()).willReturn(
                List.of(new CategoryResponse("피자", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png"),
                        new CategoryResponse("치킨", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png"))
        );

        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name")))
                .andExpect(content().string(containsString("imageUrl")));
    }

}
