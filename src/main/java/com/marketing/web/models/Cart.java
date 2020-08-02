package com.marketing.web.models;

import com.marketing.web.enums.CartStatus;
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
@Table(name = "carts")
public class Cart extends BaseModel {

    @OneToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE,orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
    private Set<CartItemHolder> items;

    @Enumerated(EnumType.STRING)
    private CartStatus cartStatus;

    public void addItem(CartItemHolder cartItemHolder){
        if (items == null){
            items = new HashSet<>();
        }
        items.add(cartItemHolder);
    }

    public void removeItem(CartItemHolder cartItemHolder){
        if (items != null){
            items.remove(cartItemHolder);
        }
    }
}