package com.marketing.web.dtos.cart;

import com.marketing.web.enums.CheckoutOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableCheckout implements Serializable {

    private List<String> sellerIds;

    private CheckoutOption checkoutOption;

}
