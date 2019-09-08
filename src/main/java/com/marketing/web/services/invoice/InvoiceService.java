package com.marketing.web.services.invoice;

import com.marketing.web.models.Invoice;
import com.marketing.web.models.User;

import java.util.List;

public interface InvoiceService {

    List<Invoice> findAll();

    Invoice findById(Long id);

    Invoice findByUUID(String uuid);

    Invoice findByOrder(String orderId);

    List<Invoice> findAllByUser(User user);

    Invoice findByOrderAndUser(String orderId, User user);

    Invoice create(Invoice invoice);

    Invoice update(Long id, Invoice updatedInvoice);

    void delete(Invoice invoice);

}
