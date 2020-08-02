package com.marketing.web.models;

import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "product_specifies")
public class ProductSpecify extends BaseModel {

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    private BigDecimal unitPrice;

    @NotNull
    private int quantity;

    @NotNull
    private double contents;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    private double commission;

    @NotNull
    private BigDecimal recommendedRetailPrice;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private Merchant merchant;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<State> states;

    @OneToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id")
    private Promotion promotion;
}
