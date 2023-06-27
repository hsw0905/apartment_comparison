package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.response.CategoryResponse;
import me.harry.baedal.domain.model.Category;
import me.harry.baedal.domain.model.CategoryId;
import me.harry.baedal.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetCategoryListServiceTest extends ServiceTest {
    @Autowired
    private GetCategoryListService getCategoryListService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        Category category1 = new Category(CategoryId.generate(), "피자", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png");
        Category category2 = new Category(CategoryId.generate(), "치킨", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png");
        Category category3 = new Category(CategoryId.generate(), "분식", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png");

        categoryRepository.saveAll(List.of(category1, category2, category3));
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("카테고리 리스트를 가져온다.")
    @Test
    void getCategoriesSuccess() {
        // when
        List<CategoryResponse> categories = getCategoryListService.getCategories();

        // then
        assertThat(categories).hasSize(3);
    }
}
