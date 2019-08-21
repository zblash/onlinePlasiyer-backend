package com.marketing.web.controllers;

import com.marketing.web.dtos.OrderDTO;
import com.marketing.web.models.CustomPrincipal;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.services.impl.OrderService;
import com.marketing.web.utils.mappers.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/bills")
    public ResponseEntity<List<OrderDTO>> getUserBills(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(orderService.findByBuyer(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_SALER')")
    @PostMapping("/sales")
    public ResponseEntity<List<OrderDTO>> getUserSales(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(orderService.findBySeller(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO).collect(Collectors.toList()));
    }

}
