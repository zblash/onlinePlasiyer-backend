package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.models.Order;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "orderFacade")
public class OrderFacadeImpl implements OrderFacade {

    @Autowired
    private OrderService orderService;

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Long id, Long sellerId) {
        Order order = orderService.findBySellerAndId(sellerId,id);

        order.setPaidPrice(writableOrder.getPaidPrice());
        order.setUnPaidPrice(writableOrder.getUnPaidPrice());
        order.setDiscount(writableOrder.getDiscount());
        order.setStatus(writableOrder.getStatus());
        order.setWaybillDate(writableOrder.getWaybillDate());

        return OrderMapper.INSTANCE.orderToReadableOrder(orderService.update(order.getId(),order));


    }
}
