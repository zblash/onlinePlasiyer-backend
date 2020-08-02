package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "orderitems")
public class OrderItem extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    private double commission;

    @NotNull
    private BigDecimal recommendedRetailPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_specify_id", referencedColumnName = "id")
    private ProductSpecify productSpecify;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    private BigDecimal discountedTotalPrice;
}
