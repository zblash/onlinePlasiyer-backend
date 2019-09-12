package com.marketing.web.controllers;

import com.marketing.web.dtos.invoice.ReadableInvoice;
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
    public ResponseEntity<List<ReadableInvoice>> getAll(){
        User user = userService.getLoggedInUser();
        List<ReadableInvoice> readableInvoices = invoiceService.findAllByUser(user).stream()
                    .map(InvoiceMapper::invoiceToReadableInvoice).collect(Collectors.toList());
        return ResponseEntity.ok(readableInvoices);
    }

    @PostMapping("/byOrder/{orderId}")
    public ResponseEntity<ReadableInvoice> getByOrder(@PathVariable String orderId){
        User user = userService.getLoggedInUser();
        Invoice invoice = invoiceService.findByOrderAndUser(orderId,user);
        return ResponseEntity.ok(InvoiceMapper.invoiceToReadableInvoice(invoice));
    }


}
