package me.harry.baedal.domain.model.category;

import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class CategoryShopId extends EntityId implements Serializable {
    protected CategoryShopId() {
        super();
    }

    public CategoryShopId(String value) {
        super(value);
    }

    public static CategoryShopId generate() {
        return new CategoryShopId(generateId());
    }
}
