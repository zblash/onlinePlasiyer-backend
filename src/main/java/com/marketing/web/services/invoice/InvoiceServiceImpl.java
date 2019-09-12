package com.marketing.web.services.invoice;

import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Invoice;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.repositories.InvoiceRepository;
import com.marketing.web.services.order.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderServiceImpl orderService;

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: "+ id));
    }

    @Override
    public Invoice findByUUID(String uuid) {
        return invoiceRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: "+ uuid));
    }

    @Override
    public Invoice findByOrder(String orderId) {
        Order order = orderService.findByUUID(orderId);
        return invoiceRepository.findByOrder(order).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with given orderId: "+ orderId));
    }

    @Override
    public List<Invoice> findAllByUser(User user) {
        if (user.getRole().getName().equals("ROLE_"+RoleType.CUSTOMER.toString())){
            return invoiceRepository.findAllByBuyer_Id(user.getId());
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.MERCHANT.toString())){
            return invoiceRepository.findAllBySeller_Id(user.getId());
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.ADMIN)){
            return invoiceRepository.findAll();
        }
        throw new ResourceNotFoundException("You have no invoice(s)");
    }

    @Override
    public Invoice findByOrderAndUser(String orderId, User user) {
        Order order = orderService.findByUUID(orderId);
        Optional<Invoice> optionalInvoice = Optional.empty();
        if (user.getRole().getName().equals("ROLE_"+RoleType.CUSTOMER.toString())){
            optionalInvoice = invoiceRepository.findByOrderAndBuyer_Id(order,user.getId());
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.MERCHANT.toString())){
            optionalInvoice =  invoiceRepository.findByOrderAndSeller_Id(order,user.getId());
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.ADMIN)){
            optionalInvoice =  invoiceRepository.findByOrder(order);
        }

        return optionalInvoice.orElseThrow(() -> new ResourceNotFoundException("Invoice not found with given orderId: "+ orderId));
    }


    @Override
    public Invoice create(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice update(Long id, Invoice updatedInvoice) {
        Invoice invoice = findById(id);
        invoice.setTotalPrice(updatedInvoice.getTotalPrice());
        invoice.setUnPaidPrice(updatedInvoice.getUnPaidPrice());
        invoice.setPaidPrice(updatedInvoice.getPaidPrice());
        invoice.setDiscount(updatedInvoice.getDiscount());
        invoice.setSeller(updatedInvoice.getSeller());
        invoice.setBuyer(updatedInvoice.getBuyer());
        invoice.setOrder(updatedInvoice.getOrder());
        return invoiceRepository.save(invoice);
    }

    @Override
    public void delete(Invoice invoice) {
        invoiceRepository.delete(invoice);
    }
}
