package com.marketing.web.controllers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.invoice.ReadableInvoice;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Invoice;
import com.marketing.web.models.User;
import com.marketing.web.services.invoice.InvoiceService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.InvoiceMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableInvoice>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        User user = userService.getLoggedInUser();
        if (UserMapper.roleToRoleType(user.getRole()).equals(RoleType.ADMIN)) {
            return ResponseEntity.ok(InvoiceMapper.pagedInvoiceListToWrapperReadableInvoice(invoiceService.findAll(pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(
                InvoiceMapper.pagedInvoiceListToWrapperReadableInvoice(invoiceService.findAllByUser(user, pageNumber, sortBy, sortType)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<WrapperPagination<ReadableInvoice>> getAllByUser(@PathVariable String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        User user = userService.findByUUID(userId);
                return ResponseEntity.ok(
                        InvoiceMapper.pagedInvoiceListToWrapperReadableInvoice(invoiceService.findAllByUser(user, pageNumber, sortBy, sortType)));

    }


    @GetMapping("/{id}")
    public ResponseEntity<ReadableInvoice> getInvoiceById(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        Invoice invoice = invoiceService.findByUUIDAndUser(id, user);
        return ResponseEntity.ok(InvoiceMapper.invoiceToReadableInvoice(invoice));
    }

    @PostMapping("/byOrder/{orderId}")
    public ResponseEntity<ReadableInvoice> getByOrder(@PathVariable String orderId) {
        User user = userService.getLoggedInUser();
        Invoice invoice = invoiceService.findByOrderAndUser(orderId, user);
        return ResponseEntity.ok(InvoiceMapper.invoiceToReadableInvoice(invoice));
    }


}
