package com.marketing.web.models;

import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "product_specifies")
public class ProductSpecify extends BaseModel {

    private UUID uuid;

    @NotNull
    private double totalPrice;

    @NotNull
    private double unitPrice;

    @NotNull
    private int quantity;

    @NotNull
    private double contents;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    private double commission;

    @NotNull
    private double recommendedRetailPrice;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<State> states;

    @OneToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id")
    private Promotion promotion;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
