package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.PaymentOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "cartitemholders")
@Builder
public class CartItemHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "cartItemHolder",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.EAGER)
    @OrderBy("id desc")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CartItem> cartItems;

    private PaymentOption paymentOption;

    private String sellerId;

    private String sellerName;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

    public void addCartItem(CartItem cartItem){
        if (cartItems == null){
            cartItems = new ArrayList<>();
        }
        cartItems.add(cartItem);
    }

    public void removeCartItem(CartItem cartItem){
        if (cartItem != null){
            cartItems.remove(cartItem);
        }
    }
}
