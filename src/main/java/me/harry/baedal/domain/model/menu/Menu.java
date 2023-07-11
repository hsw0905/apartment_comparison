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
    @JoinColumn(name = "menu_groups_id")
    private MenuGroup menuGroup;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;

    @Column(name = "image_url", length = 300)
    private String imageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder
    public Menu(MenuId id, MenuGroup menuGroup, String name, Money price, String imageUrl, String description) {
        this.id = id;
        this.menuGroup = menuGroup;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
