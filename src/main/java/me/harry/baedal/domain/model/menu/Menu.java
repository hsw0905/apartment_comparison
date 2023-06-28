package me.harry.baedal.domain.model.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.BaseEntity;
import me.harry.baedal.domain.model.common.Money;
import me.harry.baedal.domain.model.shop.Shop;

@Getter
@Entity
@Table(name = "menus")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {
    @EmbeddedId
    private MenuId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shops_id")
    private Shop shop;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "image_url", length = 300)
    private String imageUrl;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder
    public Menu(MenuId id, Shop shop, String name, String imageUrl, Money price, String description) {
        this.id = id;
        this.shop = shop;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
    }
}
