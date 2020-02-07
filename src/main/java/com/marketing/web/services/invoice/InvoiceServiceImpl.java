package com.marketing.web.services.invoice;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Invoice;
import com.marketing.web.models.Order;
import com.marketing.web.models.Product;
import com.marketing.web.models.User;
import com.marketing.web.repositories.InvoiceRepository;
import com.marketing.web.services.order.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderServiceImpl orderService;

    @Override
    public Page<Invoice> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Invoice> resultPage = invoiceRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"invoice", id.toString()));
    }

    @Override
    public Invoice findByUUID(String uuid) {
        return invoiceRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"invoice", uuid));
    }

    @Override
    public Invoice findByOrder(String orderId) {
        Order order = orderService.findByUUID(orderId);
        return invoiceRepository.findByOrder(order).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"invoice", orderId));
    }

    // TODO REFACTOR
    @Override
    public Page<Invoice> findAllByUser(User user, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Invoice> resultPage = null;
        if (user.getRole().getName().equals("ROLE_"+RoleType.CUSTOMER.toString())){
            resultPage = invoiceRepository.findAllByBuyer_Id(user.getId(), pageRequest);
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.MERCHANT.toString())){
            resultPage = invoiceRepository.findAllBySeller_Id(user.getId(), pageRequest);
        }else{
            resultPage = invoiceRepository.findAll(pageRequest);
        }

        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }

        return resultPage;
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

        return optionalInvoice.orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"invoice", orderId));
    }

    @Override
    public Invoice findByUUIDAndUser(String uuid, User user) {
        Optional<Invoice> optionalInvoice = Optional.empty();
        if (user.getRole().getName().equals("ROLE_"+RoleType.CUSTOMER.toString())){
            optionalInvoice = invoiceRepository.findByUuidAndBuyer_Id(UUID.fromString(uuid),user.getId());
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.MERCHANT.toString())){
            optionalInvoice =  invoiceRepository.findByUuidAndSeller_Id(UUID.fromString(uuid),user.getId());
        }else {
            optionalInvoice =  invoiceRepository.findByUuid(UUID.fromString(uuid));
        }

        return optionalInvoice.orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"invoice", uuid));

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

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
