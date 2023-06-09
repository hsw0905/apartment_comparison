package me.harry.baedal.domain.model.shop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "shops")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop {
    @EmbeddedId
    private ShopId id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "is_open", nullable = false)
    private boolean isOpen;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "min_order_price"))
    private Money minOrderPrice;

    @Column(name = "cumlateive_fee_rate", nullable = false)
    private BigDecimal cumulativeFeeRate;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "cumulative_fee"))
    private Money cumulativeFee;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "delivery_fee"))
    private Money deliveryFee;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "delivery_min_time")
    private LocalDateTime deliveryMinTime;

    @Column(name = "delivery_max_time")
    private LocalDateTime deliveryMaxTime;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Builder
    public Shop(ShopId id, String name, boolean isOpen, Money minOrderPrice, BigDecimal cumulativeFeeRate, Money cumulativeFee, Money deliveryFee, String description, LocalDateTime deliveryMinTime, LocalDateTime deliveryMaxTime, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
        this.minOrderPrice = minOrderPrice;
        this.cumulativeFeeRate = cumulativeFeeRate;
        this.cumulativeFee = cumulativeFee;
        this.deliveryFee = deliveryFee;
        this.description = description;
        this.deliveryMinTime = deliveryMinTime;
        this.deliveryMaxTime = deliveryMaxTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
