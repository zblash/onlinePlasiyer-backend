package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "cartitems")
public class CartItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartitemholder_id")
    @JsonIgnore
    private CartItemHolder cartItemHolder;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private ProductSpecify product;

    @OneToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id")
    private Promotion promotion;

    @NotNull
    private int quantity;

    @NotNull
    private double totalPrice;

    private double discountedTotalPrice;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

}
