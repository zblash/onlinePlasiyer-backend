package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.invoice.ReadableInvoice;
import com.marketing.web.models.Invoice;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public final class InvoiceMapper {

    public static ReadableInvoice invoiceToReadableInvoice(Invoice invoice){
        if (invoice == null) {
            return null;
        } else {
            ReadableInvoice readableInvoice = new ReadableInvoice();
            readableInvoice.setId(invoice.getUuid().toString());
            readableInvoice.setBuyer(invoice.getBuyer().getName());
            readableInvoice.setSeller(invoice.getSeller().getName());
            readableInvoice.setDiscount(invoice.getDiscount());
            readableInvoice.setPaidPrice(invoice.getPaidPrice());
            readableInvoice.setTotalPrice(invoice.getTotalPrice());
            readableInvoice.setUnPaidPrice(invoice.getUnPaidPrice());
            readableInvoice.setOrder(OrderMapper.orderToReadableOrder(invoice.getOrder()));
            return readableInvoice;
        }
    }

    public static WrapperPagination<ReadableInvoice> pagedInvoiceListToWrapperReadableInvoice(Page<Invoice> pagedInvoice){
        if (pagedInvoice == null) {
            return null;
        } else {
            WrapperPagination<ReadableInvoice> wrapperReadableInvoice = new WrapperPagination<>();
            wrapperReadableInvoice.setKey("invoices");
            wrapperReadableInvoice.setTotalPage(pagedInvoice.getTotalPages());
            wrapperReadableInvoice.setPageNumber(pagedInvoice.getNumber()+1);
            if (pagedInvoice.hasPrevious()) {
                wrapperReadableInvoice.setPreviousPage(pagedInvoice.getNumber());
            }
            if (pagedInvoice.hasNext()) {
                wrapperReadableInvoice.setNextPage(pagedInvoice.getNumber()+2);
            }
            wrapperReadableInvoice.setFirst(pagedInvoice.isFirst());
            wrapperReadableInvoice.setLast(pagedInvoice.isLast());
            wrapperReadableInvoice.setElementCountOfPage(pagedInvoice.getNumberOfElements());
            wrapperReadableInvoice.setTotalElements(pagedInvoice.getTotalElements());
            wrapperReadableInvoice.setValues(pagedInvoice.getContent().stream()
                    .map(InvoiceMapper::invoiceToReadableInvoice).collect(Collectors.toList()));
            return wrapperReadableInvoice;
        }
    }
}
