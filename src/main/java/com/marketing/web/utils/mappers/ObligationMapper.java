package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.dtos.obligation.WrapperReadableObligation;
import com.marketing.web.models.Obligation;

public final class ObligationMapper {

    public static ReadableObligation obligationToReadableObligation(Obligation obligation){
        if (obligation == null) {
            return null;
        } else {
            ReadableObligation readableObligation = new ReadableObligation();
            readableObligation.setDebt(obligation.getDebt());
            readableObligation.setReceivable(obligation.getReceivable());
            return readableObligation;
        }
    }

}
