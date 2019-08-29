package com.marketing.web.controllers;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.models.User;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    OrderFacade orderFacade;

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/bills")
    public ResponseEntity<List<ReadableOrder>> getUserBills(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(orderService.findByBuyer(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/bills/details/{id}")
    public ResponseEntity<List<ReadableOrder>> getUserBills(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(orderService.findByBuyer(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/sales")
    public ResponseEntity<List<ReadableOrder>> getUserSales(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(orderService.findBySeller(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }


    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/update/{id}")
    public ResponseEntity<ReadableOrder> updateOrder(@PathVariable Long id, WritableOrder order){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        ReadableOrder readableOrder = orderFacade.saveOrder(order,id,user.getId());
        return ResponseEntity.ok(readableOrder);
    }
}
