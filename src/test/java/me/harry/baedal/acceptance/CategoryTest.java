package me.harry.baedal.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import me.harry.baedal.domain.model.Category;
import me.harry.baedal.domain.model.CategoryId;
import me.harry.baedal.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

public class CategoryTest extends AcceptanceTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("카테고리 목록과 이미지를 가져온다.")
    @Test
    void getCategoriesSuccess() {
        // given
        Category category1 = new Category(CategoryId.generate(), "피자", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png");
        Category category2 = new Category(CategoryId.generate(), "치킨", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png");
        Category category3 = new Category(CategoryId.generate(), "분식", "https://npecbucket.s3.ap-northeast-2.amazonaws.com/test%40example.com/boards/873df9bd-6489-409e-bfa4-cb24b43173e3.png");

        categoryRepository.saveAll(List.of(category1, category2, category3));

        // when then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/categories")
                .then()
                .log().all().statusCode(HttpStatus.OK.value());

    }
}
