package com.marketing.web.services.shippingDays;

import com.marketing.web.models.Merchant;
import com.marketing.web.models.ShippingDays;
import com.marketing.web.models.State;

import java.util.List;

public interface ShippingDaysService {

    List<ShippingDays> findAll();

    List<ShippingDays> findAllByMerchant(Merchant merchant);

    ShippingDays findByMerchantAndState(Merchant merchant, State state);

    boolean hasShippingDays(Merchant merchant, State state);

    ShippingDays findByMerchantAndId(Merchant merchant, String id);

    ShippingDays findById(String id);

    ShippingDays create(ShippingDays shippingDays);

    ShippingDays update(String id, ShippingDays updatedShippingDays);

    void delete(ShippingDays shippingDays);

}
