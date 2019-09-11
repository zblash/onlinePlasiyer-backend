package com.marketing.web.dtos.cart;

import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCartItem extends DTO {

    private String id;

    private int quantity;

    private double totalPrice;

}
