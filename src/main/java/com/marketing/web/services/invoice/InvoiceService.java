package com.marketing.web.services.invoice;

import com.marketing.web.models.Invoice;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InvoiceService {

    Page<Invoice> findAll(int pageNumber);

    Invoice findById(Long id);

    Invoice findByUUID(String uuid);

    Invoice findByOrder(String orderId);

    Page<Invoice> findAllByUser(User user, int pageNumber);

    Invoice findByOrderAndUser(String orderId, User user);

    Invoice findByUUIDAndUser(String uuid, User user);

    Invoice create(Invoice invoice);

    Invoice update(Long id, Invoice updatedInvoice);

    void delete(Invoice invoice);

}
