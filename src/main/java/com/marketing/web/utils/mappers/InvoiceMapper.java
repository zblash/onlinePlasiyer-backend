package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.invoice.ReadableInvoice;
import com.marketing.web.models.Invoice;

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
            return readableInvoice;
        }
    }
}
