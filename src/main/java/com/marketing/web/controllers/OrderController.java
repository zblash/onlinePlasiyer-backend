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
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
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

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, null,  new CustomDateEditor(dateFormat, false));
    }

    //TODO Split admin route
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableOrder>> getOrders(@RequestParam(required = false) String userId, @RequestParam(required = false) String userName, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {

        User loggedInUser = userService.getLoggedInUser();
        User foundUser = null;
        Page<Order> orders;
        if (userId != null && !userId.isEmpty()) {
            foundUser = userService.findByUUID(userId);
        } else if (userName != null && !userName.isEmpty()) {
            foundUser = userService.findByUserName(userName);
        }
        endDate = (endDate != null) ? endDate : new Date();

        if (!UserMapper.roleToRoleType(loggedInUser.getRole()).equals(RoleType.ADMIN)) {
            if (foundUser != null && startDate != null) {
                orders = orderService.findAllByFilterAndUsers(startDate, endDate, loggedInUser, foundUser, pageNumber, sortBy, sortType);
            } else if (foundUser != null) {
                orders = orderService.findAllByUsers(loggedInUser, foundUser, pageNumber, sortBy, sortType);
            } else if (startDate != null) {
                orders = orderService.findAllByFilterAndUser(startDate, endDate, loggedInUser, pageNumber, sortBy, sortType);
            } else {
                orders = orderService.findAllByUser(loggedInUser, pageNumber, sortBy, sortType);
            }
        } else {
            if (foundUser != null && startDate != null) {
                orders = orderService.findAllByFilterAndUser(startDate, endDate, foundUser, pageNumber, sortBy, sortType);
            } else if (foundUser != null) {
                orders = orderService.findAllByUser(foundUser, pageNumber, sortBy, sortType);
            } else if (startDate != null) {
                orders = orderService.findAllByFilter(startDate, endDate, pageNumber, sortBy, sortType);
            } else {
                orders = orderService.findAll(pageNumber, sortBy, sortType);
            }
        }

        return ResponseEntity.ok(OrderMapper.pagedOrderListToWrapperReadableOrder(orders));
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
