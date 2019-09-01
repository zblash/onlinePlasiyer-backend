package com.marketing.web.utils.facade;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.User;

import java.util.List;

public interface OrderFacade {

    ReadableOrder saveOrder(WritableOrder writableOrder, Long id, Long sellerId);

    List<ReadableOrder> checkoutCart(User user, Cart cart, List<CartItem> cartItems);

}