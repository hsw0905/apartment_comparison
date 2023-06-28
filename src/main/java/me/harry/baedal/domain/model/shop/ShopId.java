package me.harry.baedal.domain.model.shop;

import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class ShopId extends EntityId implements Serializable {
    protected ShopId() {
    }

    public ShopId(String value) {
        super(value);
    }

    public static ShopId generate() {
        return new ShopId(generateId());
    }
}
