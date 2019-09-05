package com.marketing.web.controllers;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.models.User;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private UserService userService;

    @Autowired
    private OrderFacade orderFacade;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<ReadableOrder>> getUserBills(@RequestBody(required = false) SearchOrder searchOrder){
        User user = userService.getLoggedInUser();
        if (searchOrder != null){
            if (!searchOrder.getUserName().isEmpty() && searchOrder.getUserName() != null){
                searchOrder.setBuyerId(user.getId());
                searchOrder.setSellerId(userService.findByUserName(searchOrder.getUserName()).getId());
            }
            return ResponseEntity.ok(orderService.findAllByFilter(searchOrder).stream()
                    .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(orderService.findByBuyer(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/details/{id}")
    public ResponseEntity<List<ReadableOrder>> getUserBills(@PathVariable Long id){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(orderService.findByBuyer(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/sales")
    public ResponseEntity<List<ReadableOrder>> getUserSales(@RequestBody(required = false) SearchOrder searchOrder){
        User user = userService.getLoggedInUser();
        if (searchOrder != null){
            if (!searchOrder.getUserName().isEmpty() && searchOrder.getUserName() != null){
                searchOrder.setSellerId(user.getId());
                searchOrder.setBuyerId(userService.findByUserName(searchOrder.getUserName()).getId());
            }
            return ResponseEntity.ok(orderService.findAllByFilter(searchOrder).stream()
                    .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(orderService.findBySeller(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }


    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/update/{id}")
    public ResponseEntity<ReadableOrder> updateOrder(@PathVariable Long id, @RequestBody WritableOrder order){
        User user = userService.getLoggedInUser();
        logger.info(Double.toString(order.getDiscount()));
        ReadableOrder readableOrder = orderFacade.saveOrder(order,id,user.getId());
        return ResponseEntity.ok(readableOrder);
    }
}
