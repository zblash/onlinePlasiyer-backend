package com.marketing.web.services.invoice;

import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Invoice;
import com.marketing.web.models.User;
import com.marketing.web.repositories.InvoiceRepository;
import com.marketing.web.utils.mappers.InvoiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    @Autowired
    private InvoiceRepository invoiceRepository;


    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public Invoice findByOrder(Long orderId) {
        return invoiceRepository.findByOrder_Id(orderId).orElseThrow(RuntimeException::new);
    }

    @Override
    public List<Invoice> findAllByUser(User user) {
        if (user.getRole().getName().equals(RoleType.CUSTOMER.toString())){
            return invoiceRepository.findAllByBuyer_Id(user.getId());
        }else if (user.getRole().getName().equals(RoleType.MERCHANT.toString())){
            return invoiceRepository.findAllBySeller_Id(user.getId());
        }
        throw new RuntimeException();
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
