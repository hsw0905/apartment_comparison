package me.harry.baedal.domain.model.category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.shop.Shop;

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
