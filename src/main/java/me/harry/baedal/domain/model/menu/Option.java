package me.harry.baedal.domain.model.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.BaseEntity;
import me.harry.baedal.domain.model.common.Money;

@Getter
@Entity
@Table(name = "options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option extends BaseEntity {
    @EmbeddedId
    private OptionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_groups_id")
    private OptionGroup optionGroup;

    @Column(name = "name", length = 100)
    private String name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;

    @Builder
    public Option(OptionId id, OptionGroup optionGroup, String name, Money price) {
        this.id = id;
        this.optionGroup = optionGroup;
        this.name = name;
        this.price = price;
    }
}
