package me.harry.baedal.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "category_shops")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryShop {
    @EmbeddedId
    private CategoryShopId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categories_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shops_id")
    private Shop shop;

    @Builder
    public CategoryShop(CategoryShopId id, Category category, Shop shop) {
        this.id = id;
        this.category = category;
        this.shop = shop;
    }
}
