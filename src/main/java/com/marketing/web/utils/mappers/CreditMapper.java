package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.*;
import com.marketing.web.models.Activity;
import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public final class CreditMapper {

    public static ReadableCredit creditToReadableCredit(Credit credit) {
        if (credit == null) {
            return null;
        } else {
            ReadableCredit readableCredit = new ReadableCredit();
            readableCredit.setId(credit.getId().toString());
            readableCredit.setCreditLimit(credit.getCreditLimit());
            readableCredit.setTotalDebt(credit.getTotalDebt());
            readableCredit.setCustomerId(credit.getCustomer().getId().toString());
            readableCredit.setCustomerName(credit.getCustomer().getUser().getName());
            return readableCredit;
        }
    }

    public static Credit writableCreditToCredit(WritableCredit writableCredit){
        if (writableCredit == null){
            return null;
        } else {
            Credit systemCredit = new Credit();
            systemCredit.setCreditLimit(writableCredit.getCreditLimit());
            systemCredit.setTotalDebt(writableCredit.getTotalDebt());
            return systemCredit;
        }
    }

    public static ReadableUsersCredit usersCreditToReadableUsersCredit(Credit credit){
        if (credit == null) {
            return null;
        } else {
            ReadableUsersCredit readableUsersCredit = new ReadableUsersCredit();
            readableUsersCredit.setId(credit.getId().toString());
            readableUsersCredit.setCreditLimit(credit.getCreditLimit());
            readableUsersCredit.setTotalDebt(credit.getTotalDebt());
            readableUsersCredit.setCustomerId(credit.getCustomer().getId().toString());
            readableUsersCredit.setCustomerName(credit.getCustomer().getUser().getName());
            readableUsersCredit.setMerchantId(credit.getMerchant().getId().toString());
            readableUsersCredit.setMerchantName(credit.getMerchant().getUser().getName());
            return readableUsersCredit;
        }
    }

    public static ReadableActivity activityToReadableActivity(Activity activity) {
        if (activity == null) {
            return null;
        } else {
            ReadableActivity readableActivity = new ReadableActivity();
            readableActivity.setDate(activity.getDate());
            readableActivity.setActivityType(activity.getActivityType());
            readableActivity.setId(activity.getId().toString());
            readableActivity.setCustomerId(activity.getCustomer().getId().toString());
            readableActivity.setCustomerName(activity.getCustomer().getUser().getName());
            readableActivity.setPrice(activity.getPrice());
            readableActivity.setCurrentDebt(activity.getCurrentDebt());
            readableActivity.setCurrentReceivable(activity.getCurrentReceivable());
            readableActivity.setCreditLimit(activity.getCreditLimit());
            readableActivity.setPaymentType(activity.getPaymentType());
            readableActivity.setPaidPrice(activity.getPaidPrice());
            if (activity.getMerchant() != null) {
                readableActivity.setMerchantId(activity.getMerchant().getId().toString());
                readableActivity.setMerchantName(activity.getMerchant().getUser().getName());
            }
            return readableActivity;
        }
    }

    public static WrapperPagination<ReadableCredit> pagedCreditListToWrapperReadableCredit(Page<Credit> pagedCredit){
        if (pagedCredit == null) {
            return null;
        } else {
            WrapperPagination<ReadableCredit> wrapperReadableCredit = new WrapperPagination<>();
            wrapperReadableCredit.setKey("credits");
            wrapperReadableCredit.setTotalPage(pagedCredit.getTotalPages());
            wrapperReadableCredit.setPageNumber(pagedCredit.getNumber()+1);
            if (pagedCredit.hasPrevious()) {
                wrapperReadableCredit.setPreviousPage(pagedCredit.getNumber());
            }
            if (pagedCredit.hasNext()) {
                wrapperReadableCredit.setNextPage(pagedCredit.getNumber()+2);
            }
            wrapperReadableCredit.setFirst(pagedCredit.isFirst());
            wrapperReadableCredit.setLast(pagedCredit.isLast());
            wrapperReadableCredit.setElementCountOfPage(pagedCredit.getNumberOfElements());
            wrapperReadableCredit.setTotalElements(pagedCredit.getTotalElements());
            wrapperReadableCredit.setValues(pagedCredit.getContent().stream()
                    .map(CreditMapper::creditToReadableCredit).collect(Collectors.toList()));
            return wrapperReadableCredit;
        }
    }

    public static WrapperPagination<ReadableActivity> pagedActivityListToWrapperReadableActivity(Page<Activity> pagedActivity){
        if (pagedActivity == null) {
            return null;
        } else {
            WrapperPagination<ReadableActivity> wrapperReadableCreditActivity = new WrapperPagination<>();
            wrapperReadableCreditActivity.setKey("activities");
            wrapperReadableCreditActivity.setTotalPage(pagedActivity.getTotalPages());
            wrapperReadableCreditActivity.setPageNumber(pagedActivity.getNumber()+1);
            if (pagedActivity.hasPrevious()) {
                wrapperReadableCreditActivity.setPreviousPage(pagedActivity.getNumber());
            }
            if (pagedActivity.hasNext()) {
                wrapperReadableCreditActivity.setNextPage(pagedActivity.getNumber()+2);
            }
            wrapperReadableCreditActivity.setFirst(pagedActivity.isFirst());
            wrapperReadableCreditActivity.setLast(pagedActivity.isLast());
            wrapperReadableCreditActivity.setElementCountOfPage(pagedActivity.getNumberOfElements());
            wrapperReadableCreditActivity.setTotalElements(pagedActivity.getTotalElements());
            wrapperReadableCreditActivity.setValues(pagedActivity.getContent().stream()
                    .map(CreditMapper::activityToReadableActivity).collect(Collectors.toList()));
            return wrapperReadableCreditActivity;
        }
    }

    public static WrapperPagination<ReadableUsersCredit> pagedUsersCreditListToWrapperReadableUsersCredit(Page<Credit> pagedCredit) {
        if (pagedCredit == null) {
            return null;
        } else {
            WrapperPagination<ReadableUsersCredit> wrapperReadableCredit = new WrapperPagination<>();
            wrapperReadableCredit.setKey("usercredits");
            wrapperReadableCredit.setTotalPage(pagedCredit.getTotalPages());
            wrapperReadableCredit.setPageNumber(pagedCredit.getNumber()+1);
            if (pagedCredit.hasPrevious()) {
                wrapperReadableCredit.setPreviousPage(pagedCredit.getNumber());
            }
            if (pagedCredit.hasNext()) {
                wrapperReadableCredit.setNextPage(pagedCredit.getNumber()+2);
            }
            wrapperReadableCredit.setFirst(pagedCredit.isFirst());
            wrapperReadableCredit.setLast(pagedCredit.isLast());
            wrapperReadableCredit.setElementCountOfPage(pagedCredit.getNumberOfElements());
            wrapperReadableCredit.setTotalElements(pagedCredit.getTotalElements());
            wrapperReadableCredit.setValues(pagedCredit.getContent().stream()
                    .map(CreditMapper::usersCreditToReadableUsersCredit).collect(Collectors.toList()));
            return wrapperReadableCredit;
        }
    }
}
