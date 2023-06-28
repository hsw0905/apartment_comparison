package me.harry.baedal.domain.model.menu;

import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class OptionId extends EntityId implements Serializable {
    protected OptionId() {
        super();
    }

    public OptionId(String value) {
        super(value);
    }

    public static OptionId generate() {
        return new OptionId(generateId());
    }
}
