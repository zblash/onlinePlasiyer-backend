package com.marketing.web.controllers;

import com.marketing.web.dtos.invoice.ReadableInvoice;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Invoice;
import com.marketing.web.models.User;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.services.invoice.InvoiceServiceImpl;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.InvoiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvoiceServiceImpl invoiceService;

    @GetMapping
    public ResponseEntity<List<ReadableInvoice>> getAll(){
        User user = userService.getLoggedInUser();
        List<ReadableInvoice> readableInvoices = invoiceService.findAllByUser(user).stream()
                .map(InvoiceMapper.INSTANCE::invoiceToReadableInvoice).collect(Collectors.toList());;

        return ResponseEntity.ok(readableInvoices);
    }

    @PostMapping("/byOrder/{uuid}")
    public ResponseEntity<ReadableInvoice> getByOrder(@PathVariable String uuid){
        Invoice invoice = invoiceService.findByOrder(uuid);
        return ResponseEntity.ok(InvoiceMapper.INSTANCE.invoiceToReadableInvoice(invoice));
    }


}
