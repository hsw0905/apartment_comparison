package me.harry.baedal.domain.model;
import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class CategoryId extends EntityId implements Serializable {
    protected CategoryId() {
        super();
    }

    public CategoryId(String value) {
        super(value);
    }

    public static CategoryId generate() {
        return new CategoryId(generateId());
    }
}
