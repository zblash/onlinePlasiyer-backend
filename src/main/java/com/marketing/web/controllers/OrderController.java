package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.order.*;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
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
    public ResponseEntity<WrapperPagination<ReadableOrder>> getOrders(@RequestParam(required = false) String userId, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {

        User user = userService.getLoggedInUser();
        endDate = (endDate != null) ? endDate : new Date();

        if (UserMapper.roleToRoleType(user.getRole()).equals(RoleType.ADMIN)) {
            if (!userId.isEmpty()) {
                User userByUserId = userService.findByUUID(userId);
                if (startDate != null) {
                    return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByFilterAndUser(startDate, endDate, userByUserId, pageNumber)));
                }
                return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByUser(userByUserId, pageNumber, sortBy, sortType)));
            } else if (startDate == null) {
                return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAll(pageNumber, sortBy, sortType)));
            } else {
                return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByFilter(startDate, endDate, pageNumber)));
            }
        }

        if (!userId.isEmpty()) {
            User userByUserId = userService.findByUUID(userId);
            if (startDate != null) {
                return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByFilterAndUsers(startDate, endDate, userByUserId, pageNumber, user, userByUserId)));
            }
            return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByUsers(userByUserId, pageNumber, sortBy, sortType, user, userByUserId)));
        } else if (startDate == null) {
            return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByUser(user, pageNumber, sortBy, sortType)));
        }

        return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orderService.findAllByFilterAndUser(startDate, endDate, user, pageNumber)));


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
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT) && writableOrder.getStatus().equals(OrderStatus.CNCL)) {
            throw new BadRequestException("You can not cancel order directly");
        }
        ReadableOrder readableOrder = orderFacade.saveOrder(writableOrder, getOrder(user, id));
        return ResponseEntity.ok(readableOrder);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("confirm/{id}")
    public ResponseEntity<ReadableOrder> confirmOrder(@PathVariable String id, @Valid @RequestBody WritableConfirmOrder writableConfirmOrder) {
        User user = userService.getLoggedInUser();
        Order order = getOrder(user, id);
        return ResponseEntity.ok(orderFacade.confirmOrder(writableConfirmOrder, order));
    }

    private Order getOrder(User user, String id) {
        RoleType roleType = UserMapper.roleToRoleType(user.getRole());
        if (roleType.equals(RoleType.ADMIN)) {
            return orderService.findByUUID(id);
        }
        return orderService.findByUuidAndUser(id, user, roleType);
    }
}
