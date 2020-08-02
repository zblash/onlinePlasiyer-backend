package com.marketing.web.utils.facade;

import com.marketing.web.dtos.cart.WritableCheckout;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableConfirmOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.models.CartItemHolder;
import com.marketing.web.models.Customer;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;

import java.util.List;
import java.util.Set;

public interface OrderFacade {

    ReadableOrder saveOrder(WritableOrder writableOrder, Order order);

    ReadableOrder confirmOrder(WritableConfirmOrder writableConfirmOrder, Order order);

    List<ReadableOrder> checkoutCart(Customer customer, Set<CartItemHolder> cartItemHolderList, WritableCheckout writableCheckout);

}
