package me.harry.baedal.presentation.controller;

import me.harry.baedal.application.dto.response.CategoryResponse;
import me.harry.baedal.application.service.GetCategoryListService;
import me.harry.baedal.presentation.dto.response.common.ApiResponse;
import me.harry.baedal.presentation.dto.response.common.Type;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final GetCategoryListService getCategoryListService;

    public CategoryController(GetCategoryListService getCategoryListService) {
        this.getCategoryListService = getCategoryListService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> categories = getCategoryListService.getCategories();

        return ResponseEntity.ok(ApiResponse.of(Type.SUCCESS, HttpStatus.OK.value(), categories));
    }
}
