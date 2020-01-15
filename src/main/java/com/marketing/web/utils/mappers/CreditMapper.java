package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableCredit;
import com.marketing.web.dtos.credit.WritableCredit;
import com.marketing.web.models.SystemCredit;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public final class CreditMapper {

    public static ReadableCredit creditToReadableCredit(SystemCredit systemCredit) {
        if (systemCredit == null) {
            return null;
        } else {
            ReadableCredit readableCredit = new ReadableCredit();
            readableCredit.setId(systemCredit.getUuid().toString());
            readableCredit.setCreditLimit(systemCredit.getCreditLimit());
            readableCredit.setTotalDebt(systemCredit.getTotalDebt());
            readableCredit.setUserId(systemCredit.getUser().getUuid().toString());
            readableCredit.setUserName(systemCredit.getUser().getUsername());
            return readableCredit;
        }
    }

    public static SystemCredit writableCreditToCredit(WritableCredit writableCredit){
        if (writableCredit == null){
            return null;
        } else {
            SystemCredit systemCredit = new SystemCredit();
            systemCredit.setCreditLimit(writableCredit.getCreditLimit());
            systemCredit.setTotalDebt(writableCredit.getTotalDebt());
            return systemCredit;
        }
    }

    public static WrapperPagination<ReadableCredit> pagedOrderListToWrapperReadableOrder(Page<SystemCredit> pagedCredit){
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
