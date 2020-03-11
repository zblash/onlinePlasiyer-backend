package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.ReadableCreditActivity;
import com.marketing.web.dtos.credit.ReadableUsersCredit;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.enums.CreditType;
import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public final class CreditMapper {

    public static ReadableCredit creditToReadableCredit(Credit credit) {
        if (credit == null) {
            return null;
        } else {
            ReadableCredit readableCredit = new ReadableCredit();
            readableCredit.setId(credit.getUuid().toString());
            readableCredit.setCreditLimit(credit.getCreditLimit());
            readableCredit.setTotalDebt(credit.getTotalDebt());
            readableCredit.setCustomerId(credit.getCustomer().getUuid().toString());
            readableCredit.setCustomerName(credit.getCustomer().getUsername());
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
            readableUsersCredit.setId(credit.getUuid().toString());
            readableUsersCredit.setCreditLimit(credit.getCreditLimit());
            readableUsersCredit.setTotalDebt(credit.getTotalDebt());
            readableUsersCredit.setCustomerId(credit.getCustomer().getUuid().toString());
            readableUsersCredit.setCustomerName(credit.getCustomer().getName());
            readableUsersCredit.setMerchantId(credit.getMerchant().getUuid().toString());
            readableUsersCredit.setMerchantName(credit.getMerchant().getName());
            return readableUsersCredit;
        }
    }

    public static ReadableCreditActivity creditActivityToReadableCreditActivity(CreditActivity creditActivity) {
        if (creditActivity == null) {
            return null;
        } else {
            ReadableCreditActivity readableCreditActivity = new ReadableCreditActivity();
            readableCreditActivity.setId(creditActivity.getUuid().toString());
            readableCreditActivity.setPrice(creditActivity.getPriceValue());
            readableCreditActivity.setCreditLimit(creditActivity.getCreditLimit());
            readableCreditActivity.setTotalDebt(creditActivity.getCurrentDebt());
            readableCreditActivity.setCreditActivityType(creditActivity.getCreditActivityType());
            readableCreditActivity.setCreditType(creditActivity.getCredit().getCreditType());
            readableCreditActivity.setDocumentNo(creditActivity.getId());
            readableCreditActivity.setCustomerId(creditActivity.getCustomer().getUuid().toString());
            readableCreditActivity.setCustomerName(creditActivity.getCustomer().getName());
            if (creditActivity.getMerchant() != null) {
                readableCreditActivity.setMerchantId(creditActivity.getMerchant().getUuid().toString());
                readableCreditActivity.setMerchantName(creditActivity.getMerchant().getName());
            }
            readableCreditActivity.setDate(creditActivity.getDate());
            return readableCreditActivity;
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

    public static WrapperPagination<ReadableCreditActivity> pagedCreditActivityListToWrapperReadableCredityActivity(Page<CreditActivity> pagedCreditActivity){
        if (pagedCreditActivity == null) {
            return null;
        } else {
            WrapperPagination<ReadableCreditActivity> wrapperReadableCreditActivity = new WrapperPagination<>();
            wrapperReadableCreditActivity.setKey("creditactivities");
            wrapperReadableCreditActivity.setTotalPage(pagedCreditActivity.getTotalPages());
            wrapperReadableCreditActivity.setPageNumber(pagedCreditActivity.getNumber()+1);
            if (pagedCreditActivity.hasPrevious()) {
                wrapperReadableCreditActivity.setPreviousPage(pagedCreditActivity.getNumber());
            }
            if (pagedCreditActivity.hasNext()) {
                wrapperReadableCreditActivity.setNextPage(pagedCreditActivity.getNumber()+2);
            }
            wrapperReadableCreditActivity.setFirst(pagedCreditActivity.isFirst());
            wrapperReadableCreditActivity.setLast(pagedCreditActivity.isLast());
            wrapperReadableCreditActivity.setElementCountOfPage(pagedCreditActivity.getNumberOfElements());
            wrapperReadableCreditActivity.setTotalElements(pagedCreditActivity.getTotalElements());
            wrapperReadableCreditActivity.setValues(pagedCreditActivity.getContent().stream()
                    .map(CreditMapper::creditActivityToReadableCreditActivity).collect(Collectors.toList()));
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
