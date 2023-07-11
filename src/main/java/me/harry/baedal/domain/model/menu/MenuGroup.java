package me.harry.baedal.domain.model.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.BaseEntity;
import me.harry.baedal.domain.model.shop.Shop;

@Getter
@Entity
@Table(name = "menu_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuGroup extends BaseEntity {
    @EmbeddedId
    private MenuGroupId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shops_id")
    private Shop shop;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Builder
    public MenuGroup(MenuGroupId id, Shop shop, String name) {
        this.id = id;
        this.shop = shop;
        this.name = name;
    }
}
