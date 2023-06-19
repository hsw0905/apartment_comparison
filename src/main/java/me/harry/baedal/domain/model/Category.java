package me.harry.baedal.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.BaseEntity;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @EmbeddedId
    private CategoryId id;
    @Column(name = "name", length = 50)
    private String name;

    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
