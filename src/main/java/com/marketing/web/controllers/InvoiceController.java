package com.marketing.web.controllers;

import com.marketing.web.dtos.invoice.ReadableInvoice;
import com.marketing.web.dtos.invoice.WrapperReadableInvoice;
import com.marketing.web.models.Invoice;
import com.marketing.web.models.User;
import com.marketing.web.services.invoice.InvoiceService;
import com.marketing.web.services.invoice.InvoiceServiceImpl;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.mappers.InvoiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<WrapperReadableInvoice> getAll(@RequestParam(required = false) Integer pageNumber){
        if (pageNumber == null){
            pageNumber=1;
        }
        User user = userService.getLoggedInUser();

        return ResponseEntity.ok(
                InvoiceMapper.pagedInvoiceListToWrapperReadableInvoice(invoiceService.findAllByUser(user, pageNumber)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableInvoice> getInvoiceById(@PathVariable String id){
        User user = userService.getLoggedInUser();
        Invoice invoice = invoiceService.findByUUIDAndUser(id, user);
        return ResponseEntity.ok(InvoiceMapper.invoiceToReadableInvoice(invoice));
    }

    @PostMapping("/byOrder/{orderId}")
    public ResponseEntity<ReadableInvoice> getByOrder(@PathVariable String orderId){
        User user = userService.getLoggedInUser();
        Invoice invoice = invoiceService.findByOrderAndUser(orderId,user);
        return ResponseEntity.ok(InvoiceMapper.invoiceToReadableInvoice(invoice));
    }


}
