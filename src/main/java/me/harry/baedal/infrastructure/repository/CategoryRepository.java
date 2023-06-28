package me.harry.baedal.infrastructure.repository;

import me.harry.baedal.domain.model.category.Category;
import me.harry.baedal.domain.model.category.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, CategoryId> {

}
