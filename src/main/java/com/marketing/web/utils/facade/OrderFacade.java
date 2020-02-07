package com.marketing.web.utils.facade;

import com.marketing.web.dtos.cart.WritableCheckout;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableConfirmOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.models.Cart;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;

import java.util.List;

public interface OrderFacade {

    ReadableOrder saveOrder(WritableOrder writableOrder, Order order);

    ReadableOrder confirmOrder(WritableConfirmOrder writableConfirmOrder, Order order);

    List<ReadableOrder> checkoutCart(User user, Cart cart, WritableCheckout writableCheckout);

}
