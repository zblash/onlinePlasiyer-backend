package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.dtos.obligation.ReadableObligationActivity;
import com.marketing.web.dtos.websockets.WrapperWsNotification;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.ObligationActivity;
import com.marketing.web.models.OrderItem;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public final class ObligationMapper {

    public static ReadableObligation obligationToReadableObligation(Obligation obligation){
        if (obligation == null) {
            return null;
        } else {
            ReadableObligation readableObligation = new ReadableObligation();
            readableObligation.setId(obligation.getId().toString());
            readableObligation.setDebt(obligation.getDebt());
            readableObligation.setReceivable(obligation.getReceivable());
            readableObligation.setUserId(obligation.getMerchant().getUser().getId().toString());
            readableObligation.setUserName(obligation.getMerchant().getUser().getName());
            return readableObligation;
        }
    }

    public static ReadableObligationActivity obligationActivityToReadableObligationActivity(ObligationActivity obligationActivity) {
        if (obligationActivity == null) {
        return null;
        } else {
            ReadableObligationActivity readableObligationActivity = new ReadableObligationActivity();
            readableObligationActivity.setId(obligationActivity.getId().toString());
            readableObligationActivity.setObligationActivityType(obligationActivity.getCreditActivityType());
            readableObligationActivity.setTotalDebt(obligationActivity.getTotalDebt());
            readableObligationActivity.setTotalReceivable(obligationActivity.getTotalReceivable());
            readableObligationActivity.setDate(obligationActivity.getDate());
            readableObligationActivity.setPrice(obligationActivity.getPriceValue());
            readableObligationActivity.setUserId(obligationActivity.getMerchant().getId().toString());
            readableObligationActivity.setUserName(obligationActivity.getMerchant().getUser().getName());
            readableObligationActivity.setCustomerName(obligationActivity.getOrder().getCustomer().getUser().getName());
            readableObligationActivity.setOrderTotalPrice(obligationActivity.getOrder().getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            readableObligationActivity.setOrderCommissionPrice(obligationActivity.getOrder().getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum());
            return readableObligationActivity;
        }
    }

    public static WrapperPagination<ReadableObligation> pagedObligationListToWrapperReadableObligation(Page<Obligation> pagedObligation){
        if (pagedObligation == null) {
            return null;
        } else {
            WrapperPagination<ReadableObligation> wrapperReadableObligation = new WrapperPagination<>();
            wrapperReadableObligation.setKey("obligations");
            wrapperReadableObligation.setTotalPage(pagedObligation.getTotalPages());
            wrapperReadableObligation.setPageNumber(pagedObligation.getNumber()+1);
            if (pagedObligation.hasPrevious()) {
                wrapperReadableObligation.setPreviousPage(pagedObligation.getNumber());
            }
            if (pagedObligation.hasNext()) {
                wrapperReadableObligation.setNextPage(pagedObligation.getNumber()+2);
            }
            wrapperReadableObligation.setFirst(pagedObligation.isFirst());
            wrapperReadableObligation.setLast(pagedObligation.isLast());
            wrapperReadableObligation.setElementCountOfPage(pagedObligation.getNumberOfElements());
            wrapperReadableObligation.setTotalElements(pagedObligation.getTotalElements());
            wrapperReadableObligation.setValues(pagedObligation.getContent().stream()
                    .map(ObligationMapper::obligationToReadableObligation).collect(Collectors.toList()));
            return wrapperReadableObligation;
        }
    }


    public static WrapperPagination<ReadableObligationActivity> pagedObligationActivityListToWrapperReadableObligationActivity(Page<ObligationActivity> pagedObligationActivity) {
        if (pagedObligationActivity == null) {
            return null;
        } else {
            WrapperPagination<ReadableObligationActivity> wrapperReadableObligationActivity = new WrapperPagination<>();
            wrapperReadableObligationActivity.setKey("obligationactivities");
            wrapperReadableObligationActivity.setTotalPage(pagedObligationActivity.getTotalPages());
            wrapperReadableObligationActivity.setPageNumber(pagedObligationActivity.getNumber() + 1);
            if (pagedObligationActivity.hasPrevious()) {
                wrapperReadableObligationActivity.setPreviousPage(pagedObligationActivity.getNumber());
            }
            if (pagedObligationActivity.hasNext()) {
                wrapperReadableObligationActivity.setNextPage(pagedObligationActivity.getNumber() + 2);
            }
            wrapperReadableObligationActivity.setFirst(pagedObligationActivity.isFirst());
            wrapperReadableObligationActivity.setLast(pagedObligationActivity.isLast());
            wrapperReadableObligationActivity.setElementCountOfPage(pagedObligationActivity.getNumberOfElements());
            wrapperReadableObligationActivity.setTotalElements(pagedObligationActivity.getTotalElements());
            wrapperReadableObligationActivity.setValues(pagedObligationActivity.getContent().stream()
                    .map(ObligationMapper::obligationActivityToReadableObligationActivity).collect(Collectors.toList()));
            return wrapperReadableObligationActivity;
        }
    }
}


