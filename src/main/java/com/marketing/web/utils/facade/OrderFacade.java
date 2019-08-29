package com.marketing.web.utils.facade;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;

public interface OrderFacade {

    ReadableOrder saveOrder(WritableOrder writableOrder, Long id, Long sellerId);



}
