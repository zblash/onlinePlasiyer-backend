package com.marketing.web.controllers;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.enums.RoleType;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.models.User;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @GetMapping
    public ResponseEntity<List<ReadableOrder>> getOrders(){
        User user = userService.getLoggedInUser();

        if (UserMapper.INSTANCE.roleToRoleType(user.getRole()).equals(RoleType.ADMIN)){
            return ResponseEntity.ok(orderService.findAll().stream()
                    .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
        }

        return ResponseEntity.ok(orderService.findAllByUser(user).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<List<ReadableOrder>> getOrdersByFilter(@RequestBody SearchOrder searchOrder){
        User user = userService.getLoggedInUser();
        RoleType userRole = UserMapper.INSTANCE.roleToRoleType(user.getRole());

        searchOrder.setEndDate((searchOrder.getEndDate() == null) ? new Date() : searchOrder.getEndDate());
        return ResponseEntity.ok(orderService.findAllByFilter(searchOrder).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/details/{id}")
    public ResponseEntity<List<ReadableOrder>> getUserBills(@PathVariable String id){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(orderService.findByBuyer(user.getId()).stream()
                .map(OrderMapper.INSTANCE::orderToReadableOrder).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/update/{id}")
    public ResponseEntity<ReadableOrder> updateOrder(@PathVariable String id, @RequestBody WritableOrder order){
        User user = userService.getLoggedInUser();
        logger.info(Double.toString(order.getDiscount()));
        ReadableOrder readableOrder = orderFacade.saveOrder(order,id,user.getId());
        return ResponseEntity.ok(readableOrder);
    }
}
