package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.PaymentOption;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "cartitemholders")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CartItemHolder extends BaseModel {

    @EqualsAndHashCode.Include
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "cartItemHolder",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.EAGER)
    @OrderBy("id desc")
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<CartItem> cartItems;

    @Enumerated(EnumType.STRING)
    private PaymentOption paymentOption;

    private String sellerId;

    private String sellerName;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

    public void addCartItem(CartItem cartItem){
        if (cartItems == null){
            cartItems = new HashSet<>();
        }
        cartItems.add(cartItem);
    }

    public void removeCartItem(CartItem cartItem){
        if (cartItem != null){
            cartItems.remove(cartItem);
        }
    }
}
