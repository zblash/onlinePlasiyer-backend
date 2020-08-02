package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.PaymentOption;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "cartitemholders")
@Builder
public class CartItemHolder extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "cartItemHolder", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<CartItem> cartItems;

    @Enumerated(EnumType.STRING)
    private PaymentOption paymentOption;

    private String merchantId;

    private String merchantName;

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
