package com.marketing.web.dtos.cart;

import com.marketing.web.dtos.DTO;
import com.marketing.web.models.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO extends DTO {

    private int quantity;

    private double totalPrice;

    private List<CartItem> items;
}
