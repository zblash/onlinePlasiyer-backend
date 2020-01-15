package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.order.*;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.services.order.OrderItemService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderFacade orderFacade;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableOrder>> getOrders(@RequestParam(required = false) String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {

        User user = userService.getLoggedInUser();
        if (UserMapper.roleToRoleType(user.getRole()).equals(RoleType.ADMIN)) {
            if (!userId.isEmpty()){
                User userByUserId = userService.findByUUID(userId);
                return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByUser(userByUserId, pageNumber, sortBy, sortType)));
            }
            return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAll(pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByUser(user, pageNumber, sortBy, sortType)));
    }

    // TODO Remove
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<WrapperPagination<ReadableOrder>> getOrdersByUser(@PathVariable String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {

        User userByUserId = userService.findByUUID(userId);
        return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByUser(userByUserId, pageNumber, sortBy, sortType)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/summary")
    public ResponseEntity<OrderSummary> getOrderSummary() {
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(orderService.groupBy(user));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/summary/byUser/{userId}")
    public ResponseEntity<OrderSummary> getUserOrderSummary(@PathVariable String userId) {
        User user = userService.findByUUID(userId);
        return ResponseEntity.ok(orderService.groupBy(user));
    }

    @PostMapping("/filter")
    public ResponseEntity<WrapperPagination<ReadableOrder>> getOrdersByFilter(@RequestBody SearchOrder searchOrder, @RequestParam(defaultValue = "1") Integer pageNumber) {
        User user = userService.getLoggedInUser();
        RoleType userRole = UserMapper.roleToRoleType(user.getRole());

        searchOrder.setEndDate((searchOrder.getEndDate() == null) ? new Date() : searchOrder.getEndDate());

        if (UserMapper.roleToRoleType(user.getRole()).equals(RoleType.ADMIN)) {
            return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByFilter(searchOrder, pageNumber)));
        }

        return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByFilterAndUser(searchOrder, user, pageNumber)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableOrder> getUserOrder(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(OrderMapper.orderToReadableOrder(getOrder(user, id)));
    }

    @PostMapping("/items/{id}")
    public ResponseEntity<List<ReadableOrderItem>> getUserOrderDetails(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        Order order = getOrder(user, id);
        return ResponseEntity.ok(orderItemService.findByOrder(order).stream()
                .map(OrderMapper::orderItemToReadableOrderItem).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableOrder> updateOrder(@PathVariable String id, @Valid @RequestBody WritableOrder writableOrder) {
        User user = userService.getLoggedInUser();
        ReadableOrder readableOrder = orderFacade.saveOrder(writableOrder, getOrder(user, id));
        return ResponseEntity.ok(readableOrder);

    }

    private Order getOrder(User user, String id) {
        RoleType roleType = UserMapper.roleToRoleType(user.getRole());
        if (roleType.equals(RoleType.ADMIN)) {
            return orderService.findByUUID(id);
        }
        return orderService.findByUuidAndUser(id, user, roleType);
    }
}
