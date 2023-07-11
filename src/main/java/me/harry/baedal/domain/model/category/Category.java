package me.harry.baedal.domain.model.category;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.BaseEntity;

@Entity
@Getter
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @EmbeddedId
    private CategoryId id;
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    @Column(name = "image_url", length = 300)
    private String imageUrl;

    public Category(CategoryId id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
