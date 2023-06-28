package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.response.CategoryResponse;
import me.harry.baedal.domain.model.category.Category;
import me.harry.baedal.infrastructure.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetCategoryListService {
    private final CategoryRepository categoryRepository;

    public GetCategoryListService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryResponse(category.getName(), category.getImageUrl()))
                .toList();
    }
}
