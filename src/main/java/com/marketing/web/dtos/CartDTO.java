package com.marketing.web.dtos;

import com.marketing.web.models.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private int quantity;

    private double totalPrice;

    private List<CartItem> items;
}
