package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.shippingDays.ReadableShippingDays;
import com.marketing.web.models.ShippingDays;

public final class ShippingDaysMapper {

    public static ReadableShippingDays shippingDaysToReadableShippingDays(ShippingDays shippingDays) {
        if (shippingDays == null) {
            return null;
        }
        ReadableShippingDays readableShippingDays = new ReadableShippingDays();
        readableShippingDays.setId(shippingDays.getId().toString());
        readableShippingDays.setMerchantId(shippingDays.getMerchant().getId().toString());
        readableShippingDays.setMerchantName(shippingDays.getMerchant().getUser().getName());
        readableShippingDays.setCityId(shippingDays.getState().getCity().getId().toString());
        readableShippingDays.setCityName(shippingDays.getState().getCity().getTitle());
        readableShippingDays.setStateId(shippingDays.getState().getId().toString());
        readableShippingDays.setStateName(shippingDays.getState().getTitle());
        readableShippingDays.setDays(shippingDays.getDays());
        return readableShippingDays;
    }

}
