package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public final class CreditMapper {

    public static ReadableCredit creditToReadableCredit(Credit credit) {
        if (credit == null) {
            return null;
        } else {
            ReadableCredit readableCredit = new ReadableCredit();
            readableCredit.setCreditLimit(credit.getCreditLimit());
            readableCredit.setTotalDebt(credit.getTotalDebt());
            readableCredit.setUserId(credit.getUser().getUuid().toString());
            readableCredit.setUserName(credit.getUser().getUsername());
            return readableCredit;
        }
    }

    public static Credit writableCreditToCredit(WritableCredit writableCredit){
        if (writableCredit == null){
            return null;
        } else {
            Credit credit = new Credit();
            credit.setCreditLimit(writableCredit.getCreditLimit());
            credit.setCreditLimit(writableCredit.getTotalDebt());
            return credit;
        }
    }

    public static WrapperPagination<ReadableCredit> pagedOrderListToWrapperReadableOrder(Page<Credit> pagedCredit){
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
}
