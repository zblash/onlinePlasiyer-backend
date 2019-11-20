package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.models.Obligation;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public final class ObligationMapper {

    public static ReadableObligation obligationToReadableObligation(Obligation obligation){
        if (obligation == null) {
            return null;
        } else {
            ReadableObligation readableObligation = new ReadableObligation();
            readableObligation.setId(obligation.getUuid().toString());
            readableObligation.setDebt(obligation.getDebt());
            readableObligation.setReceivable(obligation.getReceivable());
            return readableObligation;
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

}
