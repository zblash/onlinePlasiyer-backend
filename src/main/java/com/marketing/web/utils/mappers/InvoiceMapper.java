package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.invoice.ReadableInvoice;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.models.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceMapper INSTANCE = Mappers.getMapper( InvoiceMapper.class );

    default ReadableInvoice invoiceToReadableInvoice(Invoice invoice){
        ReadableInvoice readableInvoice = new ReadableInvoice();
        readableInvoice.setId(invoice.getUuid().toString());
        readableInvoice.setBuyer(invoice.getBuyer().getName());
        readableInvoice.setSeller(invoice.getSeller().getName());
        readableInvoice.setDiscount(invoice.getDiscount());
        readableInvoice.setPaidPrice(invoice.getPaidPrice());
        readableInvoice.setTotalPrice(invoice.getTotalPrice());
        readableInvoice.setUnPaidPrice(invoice.getUnPaidPrice());
        return readableInvoice;
    }
}
