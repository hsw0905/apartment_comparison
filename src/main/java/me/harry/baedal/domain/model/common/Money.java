package me.harry.baedal.domain.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {
    @Column(name = "amount")
    private BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }
}
