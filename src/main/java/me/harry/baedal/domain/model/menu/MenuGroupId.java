package me.harry.baedal.domain.model.menu;

import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class MenuGroupId extends EntityId implements Serializable {
    protected MenuGroupId() {
        super();
    }

    public MenuGroupId(String value) {
        super(value);
    }

    public static MenuGroupId generate() {
        return new MenuGroupId(generateId());
    }
}
